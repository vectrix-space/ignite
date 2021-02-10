package com.mineteria.ignite.event;

import static java.util.Objects.requireNonNull;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.Multimaps;
import com.mineteria.ignite.api.event.EventHandler;
import com.mineteria.ignite.api.event.EventManager;
import com.mineteria.ignite.api.event.Subscribe;
import com.mineteria.ignite.api.event.SubscribePriority;
import com.mineteria.ignite.mod.ModEngine;
import net.kyori.event.EventSubscriber;
import net.kyori.event.PostResult;
import net.kyori.event.SimpleEventBus;
import net.kyori.event.method.MethodScanner;
import net.kyori.event.method.MethodSubscriptionAdapter;
import net.kyori.event.method.SimpleMethodSubscriptionAdapter;
import net.kyori.event.method.asm.ASMEventExecutorFactory;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.IdentityHashMap;

public final class IgniteEventManager implements EventManager {
  private final ListMultimap<Object, Object> registeredListenersByMod = Multimaps
    .synchronizedListMultimap(Multimaps.newListMultimap(new IdentityHashMap<>(), ArrayList::new));
  private final ListMultimap<Object, EventHandler<?>> registeredHandlersByMod = Multimaps
    .synchronizedListMultimap(Multimaps.newListMultimap(new IdentityHashMap<>(), ArrayList::new));
  private final SimpleEventBus<Object> bus;
  private final MethodSubscriptionAdapter<Object> methodAdapter;
  private final ModEngine engine;

  public IgniteEventManager(final @NonNull ModEngine engine) {
    this.engine = engine;

    this.bus = new SimpleEventBus<Object>(Object.class) {
      @Override
      protected boolean shouldPost(final @NonNull Object event, final @NonNull EventSubscriber<?> subscriber) {
        return true;
      }
    };

    this.methodAdapter = new SimpleMethodSubscriptionAdapter<>(
      bus,
      new ASMEventExecutorFactory<>(ClassLoader.getSystemClassLoader()),
      new IgniteMethodScanner()
    );
  }

  @Override
  public void register(final @NonNull Object mod, final @NonNull Object listener) {
    requireNonNull(mod, "mod");
    requireNonNull(listener, "listener");

    this.ensureMod(mod);

    if (mod == listener && this.registeredListenersByMod.containsEntry(mod, mod)) {
      throw new IllegalArgumentException("The mod instance if automatically registered!");
    }

    this.registeredListenersByMod.put(mod, listener);
    this.methodAdapter.register(listener);
  }

  @Override
  public <E> void register(final @NonNull Object mod, final @NonNull Class<E> event,
                           final @NonNull SubscribePriority priority, final @NonNull EventHandler<E> handler) {
    requireNonNull(mod, "mod");
    requireNonNull(event, "event");
    requireNonNull(priority, "priority");
    requireNonNull(handler, "handler");

    this.ensureMod(mod);

    this.registeredHandlersByMod.put(mod, handler);
    this.bus.register(event, new KyoriToIgniteHandler<>(handler, priority));
  }

  @Override
  public void unregister(final @NonNull Object mod) {
    requireNonNull(mod, "mod");

    this.ensureMod(mod);

    final Collection<Object> listeners = this.registeredListenersByMod.removeAll(mod);
    final Collection<EventHandler<?>> handlers = this.registeredHandlersByMod.removeAll(mod);
    listeners.forEach(this.methodAdapter::unregister);
    handlers.forEach(this::unregisterHandler);
  }

  @Override
  public void unregister(final @NonNull Object mod, final @NonNull Object listener) {
    requireNonNull(mod, "mod");

    this.ensureMod(mod);

    if (this.registeredListenersByMod.remove(mod, listener)) {
      this.methodAdapter.unregister(listener);
    }
  }

  @Override
  public <E> void unregister(final @NonNull Object mod, final @NonNull EventHandler<E> handler) {
    requireNonNull(mod, "mod");

    this.ensureMod(mod);

    if (this.registeredHandlersByMod.remove(mod, handler)) {
      this.unregisterHandler(handler);
    }
  }

  @Override
  public void post(final @NonNull Object event) {
    requireNonNull(event, "event");

    if (!this.bus.hasSubscribers(event.getClass())) return;

    final PostResult result = this.bus.post(event);
    if (!result.exceptions().isEmpty()) {
      this.engine.getLogger().error("An error occurred attempting to post an event '{}'!", event);

      int i = 0;
      for (final Throwable throwable : result.exceptions().values()) {
        this.engine.getLogger().error("#{}: \n", ++i, throwable);
      }
    }
  }

  private void unregisterHandler(final EventHandler<?> handler) {
    this.bus.unregister(subscriber -> subscriber instanceof KyoriToIgniteHandler && ((KyoriToIgniteHandler<?>) subscriber).handler == handler);
  }

  private void ensureMod(final @NonNull Object mod) {
    if (!this.engine.isMod(mod)) throw new IllegalArgumentException("Specified mod is not loaded!");
  }

  private static class IgniteMethodScanner implements MethodScanner<Object> {
    @Override
    public boolean shouldRegister(final @NonNull Object listener, final @NonNull Method method) {
      return method.isAnnotationPresent(Subscribe.class);
    }

    @Override
    public int postOrder(final @NonNull Object listener, final @NonNull Method method) {
      return method.getAnnotation(Subscribe.class).priority().ordinal();
    }

    @Override
    public boolean consumeCancelledEvents(final @NonNull Object listener, final @NonNull Method method) {
      return true;
    }
  }

  private static class KyoriToIgniteHandler<E> implements EventSubscriber<E> {
    private final EventHandler<E> handler;
    private final int priority;

    private KyoriToIgniteHandler(final @NonNull EventHandler<E> handler, final @NonNull SubscribePriority priority) {
      this.handler = handler;
      this.priority = priority.ordinal();
    }

    @Override
    public void invoke(final @NonNull E event) throws Throwable {
      handler.execute(event);
    }

    @Override
    public int postOrder() {
      return this.priority;
    }
  }
}
