package play.libs;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.Arrays;

import play.api.libs.concurrent.PlayPromise;
import play.core.j.FPromiseHelper;
import scala.concurrent.ExecutionContext;
import scala.concurrent.Future;

/**
 * Defines a set of functional programming style helpers.
 */
public class F {

    /**
     * A Callback with no arguments.
     */
    public static interface Callback0 {
        public void invoke() throws Throwable;
    }

    /**
     * A Callback with a single argument.
     */
    public static interface Callback<A> {
        public void invoke(A a) throws Throwable;
    }

    /**
     * A Callback with 2 arguments.
     */
    public static interface Callback2<A,B> {
        public void invoke(A a, B b) throws Throwable;
    }

    /**
     * A Callback with 3 arguments.
     */
    public static interface Callback3<A,B,C> {
        public void invoke(A a, B b, C c) throws Throwable;
    }

    /**
     * A Function with no arguments.
     */
    public static interface Function0<R> {
        public R apply() throws Throwable;
    }

    /**
     * A Function with a single argument.
     */
    public static interface Function<A,R> {
        public R apply(A a) throws Throwable;
    }

    /**
     * A Function with 2 arguments.
     */
    public static interface Function2<A,B,R> {
        public R apply(A a, B b) throws Throwable;
    }

    /**
     * A Function with 3 arguments.
     */
    public static interface Function3<A,B,C,R> {
        public R apply(A a, B b, C c) throws Throwable;
    }

    /**
     * A promise to produce a result of type <code>A</code>.
     */
    public static class Promise<A> {

        private final Future<A> future;

        /**
         * Creates a Promise that wraps a Scala Future.
         *
         * @param future The Scala Future to wrap
         * @deprecated Since 2.2. Use {@link #wrap(Future)} instead.
         */
        @Deprecated
        public Promise(Future<A> future) {
            this.future = future;
        }

        /**
         * Creates a Promise that wraps a Scala Future.
         *
         * @param future The Scala Future to wrap
         */
        @SuppressWarnings("deprecation")
        public static <A> Promise<A> wrap(Future<A> future) {
            return new Promise<A>(future);
        }

        /**
         * Combine the given promises into a single promise for the list of results.
         *
         * The sequencing operations are performed in the default ExecutionContext.
         *
         * @param promises The promises to combine
         * @return A single promise whose methods act on the list of redeemed promises
         */
        public static <A> Promise<List<A>> sequence(Promise<? extends A>... promises){
            return FPromiseHelper.<A>sequence(java.util.Arrays.asList(promises), HttpExecution.defaultContext());
        }

        /**
         * Combine the given promises into a single promise for the list of results.
         *
         * @param ec Used to execute the sequencing operations.
         * @param promises The promises to combine
         * @return A single promise whose methods act on the list of redeemed promises
         */
        public static <A> Promise<List<A>> sequence(ExecutionContext ec, Promise<? extends A>... promises){
            return FPromiseHelper.<A>sequence(java.util.Arrays.asList(promises), ec);
        }

        /**
         * Combine the given promises into a single promise for the list of results.
         *
         * @param promises The promises to combine
         * @return A single promise whose methods act on the list of redeemed promises
         * @deprecated Since 2.1. Use {@link #sequence(Promise...)} instead.
         */
        @Deprecated
        public static <A> Promise<List<A>> waitAll(Promise<? extends A>... promises){
            return sequence(promises);
        }

        /**
         * Create a Promise that is redeemed after a timeout.
         *
         * @param message The message to use to redeem the Promise.
         * @param delay The delay (expressed with the corresponding unit).
         * @param unit The Unit.
         */
        public static <A> Promise<A> timeout(A message, long delay, TimeUnit unit) {
            return FPromiseHelper.timeout(message, delay, unit);
        }

        /**
         * Create a Promise that is redeemed after a timeout.
         *
         * @param message The message to use to redeem the Promise.
         * @param delay The delay (expressed with the corresponding unit).
         * @param unit The Unit.
         * @deprecated Since 2.2. Use {@link #timeout(Object, long, TimeUnit)} instead.
         */
        @Deprecated
        public static <A> Promise<A> timeout(A message, Long delay, TimeUnit unit) {
            return FPromiseHelper.timeout(message, delay, unit);
        }

        /**
         * Create a Promise timer that throws a TimeoutException after the
         * default timeout duration expires.
         *
         * The returned Promise is usually combined with other Promises.
         *
         * @return a promise without a real value
         * @deprecated Since 2.2. Use {@link #timeout(long)} or {@link #timeout(long, TimeUnit)} instead.
         */
        @Deprecated
        public static Promise<scala.Unit> timeout() throws TimeoutException {
            return FPromiseHelper.timeout(FPromiseHelper.defaultTimeout(), TimeUnit.MILLISECONDS);
        }

        /**
         * Create a Promise that is redeemed after a timeout.
         *
         * @param message The message to use to redeem the Promise.
         * @param delay The delay expressed in milliseconds.
         */
        public static <A> Promise<A> timeout(A message, long delay) {
            return timeout(message, delay, TimeUnit.MILLISECONDS);
        }

        /**
         * Create a Promise that is redeemed after a timeout.
         *
         * @param message The message to use to redeem the Promise.
         * @param delay The delay expressed in milliseconds.
         * @deprecated Since 2.2. Use {@link #timeout(Object, long)} instead.
         */
        @Deprecated
        public static <A> Promise<A> timeout(A message, Long delay) {
            return timeout(message, delay.longValue(), TimeUnit.MILLISECONDS);
        }

        /**
         * Create a Promise timer that throws a TimeoutException after a
         * given timeout.
         *
         * The returned Promise is usually combined with other Promises.
         *
         * @return a promise without a real value
         * @param delay The delay expressed in milliseconds.
         */
        public static <A> Promise<scala.Unit> timeout(long delay) {
            return timeout(delay, TimeUnit.MILLISECONDS);
        }

        /**
         * Create a Promise timer that throws a TimeoutException after a
         * given timeout.
         *
         * The returned Promise is usually combined with other Promises.
         *
         * @param delay The delay (expressed with the corresponding unit).
         * @param unit The Unit.
         * @return a promise without a real value
         */
        public static <A> Promise<scala.Unit> timeout(long delay, TimeUnit unit) {
            return FPromiseHelper.timeout(delay, unit);
        }

        /**
         * Combine the given promises into a single promise for the list of results.
         *
         * The sequencing operations are performed in the default ExecutionContext.

         * @param promises The promises to combine
         * @return A single promise whose methods act on the list of redeemed promises
         */
        public static <A> Promise<List<A>> sequence(Iterable<Promise<? extends A>> promises){
            return FPromiseHelper.<A>sequence(promises, HttpExecution.defaultContext());
        }

        /**
         * Combine the given promises into a single promise for the list of results.
         *
         * @param promises The promises to combine
         * @param ec Used to execute the sequencing operations.
         * @return A single promise whose methods act on the list of redeemed promises
         */
        public static <A> Promise<List<A>> sequence(Iterable<Promise<? extends A>> promises, ExecutionContext ec){
            return FPromiseHelper.<A>sequence(promises, ec);
        }

        /**
         * Combine the given promises into a single promise for the list of results.
         *
         * @param promises The promises to combine
         * @return A single promise whose methods act on the list of redeemed promises
         * @deprecated Since 2.1. Use {@link #sequence(Iterable)} instead.
         */
        @Deprecated
        public static <A> Promise<List<A>> waitAll(Iterable<Promise<? extends A>> promises){
            return sequence(promises);
        }

        /**
         * Create a new pure promise, that is, a promise with a constant value from the start.
         *
         * @param a the value for the promise
         */
        public static <A> Promise<A> pure(final A a) {
            return FPromiseHelper.pure(a);
        }

        /**
         * Create a new promise throwing an exception.
         * @param throwable Value to throw
         */
        public static <A> Promise<A> throwing(Throwable throwable) {
            return FPromiseHelper.throwing(throwable);
        }

        /**
         * Create a Promise which will be redeemed with the result of a given function.
         *
         * The Function0 will be run in the default ExecutionContext.
         *
         * @param function Used to fulfill the Promise.
         */
        public static <A> Promise<A> promise(Function0<A> function) {
            return FPromiseHelper.promise(function, HttpExecution.defaultContext());
        }

        /**
         * Create a Promise which will be redeemed with the result of a given Function0.
         *
         * @param function Used to fulfill the Promise.
         * @param ec The ExecutionContext to run the function in.
         */
        public static <A> Promise<A> promise(Function0<A> function, ExecutionContext ec) {
            return FPromiseHelper.promise(function, ec);
        }

        /**
         * Create a Promise which, after a delay, will be redeemed with the result of a
         * given function. The function will be called after the delay.
         *
         * The function will be run in the default ExecutionContext.
         *
         * @param function The function to call to fulfill the Promise.
         * @param delay The time to wait.
         * @param unit The units to use for the delay.
         */
        public static <A> Promise<A> delayed(Function0<A> function, long delay, TimeUnit unit) {
            return FPromiseHelper.delayed(function, delay, unit, HttpExecution.defaultContext());
        }

        /**
         * Create a Promise which, after a delay, will be redeemed with the result of a
         * given function. The function will be called after the delay.
         *
         * @param function The function to call to fulfill the Promise.
         * @param delay The time to wait.
         * @param unit The units to use for the delay.
         * @param ec The ExecutionContext to run the Function0 in.
         */
        public static <A> Promise<A> delayed(Function0<A> function, long delay, TimeUnit unit, ExecutionContext ec) {
            return FPromiseHelper.delayed(function, delay, unit, ec);
        }

        /**
         * Awaits for the promise to get the result using a default timeout
         * (currently 10000 milliseconds).<br>
         * Throws a Throwable if the calculation providing the promise threw an exception
         *
         * @return The promised object
         * @deprecated Since 2.2. Use {@link #get(long, TimeUnit)} or {@link #get(long)} instead.
         */
        @Deprecated
        public A get() {
            return FPromiseHelper.get(this, FPromiseHelper.defaultTimeout(), TimeUnit.MILLISECONDS);
        }

        /**
         * Awaits for the promise to get the result.<br>
         * Throws a Throwable if the calculation providing the promise threw an exception
         *
         * @param timeout A user defined timeout
         * @param unit timeout for timeout
         * @return The promised result
         * @deprecated Since 2.2. Use {@link #get(long, TimeUnit)} instead.
         */
        @Deprecated
        public A get(Long timeout, TimeUnit unit) {
            return FPromiseHelper.get(this, timeout, unit);
        }

        /**
         * Awaits for the promise to get the result.
         * Throws a Throwable if the calculation providing the promise threw an exception
         *
         * @param timeout A user defined timeout
         * @param unit timeout for timeout
         * @return The promised result
         */
        public A get(long timeout, TimeUnit unit) {
            return FPromiseHelper.get(this, timeout, unit);
        }

        /**
         * Awaits for the promise to get the result.<br>
         * Throws a Throwable if the calculation providing the promise threw an exception
         *
         * @param timeout A user defined timeout in milliseconds
         * @return The promised result
         * @deprecated Since 2.2. Use {{@link #get(long)} instead.
         */
        @Deprecated
        public A get(Long timeout) {
            return FPromiseHelper.get(this, timeout, TimeUnit.MILLISECONDS);
        }

        /**
         * Awaits for the promise to get the result.
         *
         * @param timeout A user defined timeout in milliseconds
         * @return The promised result
         */
        public A get(long timeout) {
            return FPromiseHelper.get(this, timeout, TimeUnit.MILLISECONDS);
        }

        /**
         * combines the current promise with <code>another</code> promise using `or`
         * @param another 
         */
        public <B> Promise<Either<A,B>> or(Promise<B> another) {
            return (wrap(new PlayPromise(this.future).or(another.wrapped()))).map(
              new  play.core.j.EitherToFEither<A,B>()
            );
        }

        /**
         * Perform the given <code>action</code> callback when the Promise is redeemed.
         * 
         * The callback will be run in the default execution context.
         *
         * @param action The action to perform.
         */
        public void onRedeem(final Callback<A> action) {
            FPromiseHelper.onRedeem(this, action, HttpExecution.defaultContext());
        }

        /**
         * Perform the given <code>action</code> callback when the Promise is redeemed.
         *
         * @param action The action to perform.
         * @param ec The ExecutionContext to execute the action in.
         */
        public void onRedeem(final Callback<A> action, ExecutionContext ec) {
            FPromiseHelper.onRedeem(this, action, ec);
        }

        /**
         * Maps this promise to a promise of type <code>B</code>.  The function <code>function</code> is applied as
         * soon as the promise is redeemed.
         *
         * The function will be run in the default execution context.

         * @param function The function to map <code>A</code> to <code>B</code>.
         * @return A wrapped promise that maps the type from <code>A</code> to <code>B</code>.
         */
        public <B> Promise<B> map(final Function<A, B> function) {
            return FPromiseHelper.map(this, function, HttpExecution.defaultContext());
        }

        /**
         * Maps this promise to a promise of type <code>B</code>.  The function <code>function</code> is applied as
         * soon as the promise is redeemed.
         *
         * @param function The function to map <code>A</code> to <code>B</code>.
         * @param ec The ExecutionContext to execute the function in.
         * @return A wrapped promise that maps the type from <code>A</code> to <code>B</code>.
         */
        public <B> Promise<B> map(final Function<A, B> function, ExecutionContext ec) {
            return FPromiseHelper.map(this, function, ec);
        }

        /**
         * Wraps this promise in a promise that will handle exceptions thrown by this Promise.
         *
         * The function will be run in the default execution context.

         * @param function The function to handle the exception. This may, for example, convert the exception into something
         *      of type <code>T</code>, or it may throw another exception, or it may do some other handling.
         * @return A wrapped promise that will only throw an exception if the supplied <code>function</code> throws an
         *      exception.
         */
        public Promise<A> recover(final Function<Throwable,A> function) {
            return FPromiseHelper.recover(this, function, HttpExecution.defaultContext());
        }

        /**
         * Wraps this promise in a promise that will handle exceptions thrown by this Promise.
         *
         * @param function The function to handle the exception. This may, for example, convert the exception into something
         *      of type <code>T</code>, or it may throw another exception, or it may do some other handling.
         * @param ec The ExecutionContext to execute the function in.
         * @return A wrapped promise that will only throw an exception if the supplied <code>function</code> throws an
         *      exception.
         */
        public Promise<A> recover(final Function<Throwable,A> function, ExecutionContext ec) {
            return FPromiseHelper.recover(this, function, ec);
        }

        /**
         * Perform the given <code>action</code> callback if the promise encounters an exception.
         *
         * This action will be run in the default exceution context.
         *
         * @param action The action to perform.
         */
        public void onFailure(final Callback<Throwable> action) {
            FPromiseHelper.onFailure(this, action, HttpExecution.defaultContext());
        }

        /**
         * Perform the given <code>action</code> callback if the promise encounters an exception.
         *
         * @param action The action to perform.
         * @param ec The ExecutionContext to execute the callback in.
         */
        public void onFailure(final Callback<Throwable> action, ExecutionContext ec) {
            FPromiseHelper.onFailure(this, action, ec);
        }

        /**
         * Maps the result of this promise to a promise for a result of type <code>B</code>, and flattens that to be
         * a single promise for <code>B</code>.
         *
         * The function will be run in the default execution context.
         *
         * @param function The function to map <code>A</code> to a promise for <code>B</code>.
         * @return A wrapped promise for a result of type <code>B</code>
         */
        public <B> Promise<B> flatMap(final Function<A,Promise<B>> function) {
            return FPromiseHelper.flatMap(this, function, HttpExecution.defaultContext());
        }

        /**
         * Maps the result of this promise to a promise for a result of type <code>B</code>, and flattens that to be
         * a single promise for <code>B</code>.
         *
         * @param function The function to map <code>A</code> to a promise for <code>B</code>.
         * @param ec The ExecutionContext to execute the function in.
         * @return A wrapped promise for a result of type <code>B</code>
         */
        public <B> Promise<B> flatMap(final Function<A,Promise<B>> function, ExecutionContext ec) {
            return FPromiseHelper.flatMap(this, function, ec);
        }

        /**
         * Zips the values of this promise with <code>another</code>, and creates a new promise holding the tuple of their results
         * @param another
         */
        public <B> Promise<Tuple<A, B>> zip(Promise<B> another) {
            return wrap(wrapped().zip(another.wrapped())).map(
                new Function<scala.Tuple2<A, B>, Tuple<A, B>>() {
                    public Tuple<A, B> apply(scala.Tuple2<A, B> scalaTuple) {
                        return new Tuple(scalaTuple._1, scalaTuple._2);
                    }
                }
            );
        }

        /**
         * Gets the Scala Future wrapped by this Promise.
         *
         * @return The Scala Future
         */
        public Future<A> wrapped() {
            return future;
        }

        /**
         * Gets the Scala Future wrapped by this Promise.
         *
         * @return The Scala Future
         * @deprecated Since 2.2. Use {@link #wrapped()} instead.
         */
        @Deprecated
        public Future<A> getWrappedPromise() {
            return wrapped();
        }

    }

    /**
     * Represents optional values. Instances of <code>Option</code> are either an instance of <code>Some</code> or the object <code>None</code>.
     */
    public static abstract class Option<T> implements Collection<T> {

        /**
         * Is the value of this option defined?
         *
         * @return <code>true</code> if the value is defined, otherwise <code>false</code>.
         */
        public abstract boolean isDefined();

        @Override
        public boolean isEmpty() {
            return !isDefined();
        }

        /**
         * Returns the value if defined.
         *
         * @return The value if defined, otherwise <code>null</code>.
         */
        public abstract T get();

        /**
         * Constructs a <code>None</code> value.
         *
         * @return None
         */
        public static <T> None<T> None() {
            return new None<T>();
        }

        /**
         * Construct a <code>Some</code> value.
         *
         * @param value The value to make optional
         * @return Some <code>T</code>.
         */
        public static <T> Some<T> Some(T value) {
            return new Some<T>(value);
        }

        /**
         * Get the value if defined, otherwise return the supplied <code>defaultValue</code>.
         *
         * @param defaultValue The value to return if the value of this option is not defined
         * @return The value of this option, or <code>defaultValue</code>.
         */
        public T getOrElse(T defaultValue) {
            if(isDefined()) {
                return get();
            } else {
                return defaultValue;
            }
        }

        /**
         * Map this option to another value.
         *
         * @param function The function to map the option using.
         * @return The mapped option.
         * @throws RuntimeException if <code>function</code> threw an Exception.  If the exception is a
         *      <code>RuntimeException</code>, it will be rethrown as is, otherwise it will be wrapped in a
         *      <code>RuntimeException</code>.
         */
        public <A> Option<A> map(Function<T,A> function) {
            if(isDefined()) {
                try {
                    return Some(function.apply(get()));
                } catch (RuntimeException e) {
                    throw e;
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
            } else {
                return None();
            }
        }

        @Override
        public void clear() {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean add(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean addAll(Collection<? extends T> c) {
            throw new UnsupportedOperationException();
        }

    }

    /**
     * Construct a <code>Some</code> value.
     *
     * @param value The value
     * @return Some value.
     */
    public static <A> Some<A> Some(A value) {
        return new Some<A>(value);
    }

    /**
     * Constructs a <code>None</code> value.
     *
     * @return None.
     */
    public static None None() {
        return new None();
    }

    /**
     * Represents non-existent values.
     */
    public static class None<T> extends Option<T> {

        @Override
        public boolean isDefined() {
            return false;
        }

        @Override
        public int size() {
            return 0;
        }

        @Override
        public T get() {
            throw new IllegalStateException("No value");
        }

        public Iterator<T> iterator() {
            return Collections.<T>emptyList().iterator();
        }

        @Override
        public boolean contains(Object o) {
            return false;
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return c.size() == 0;
        }

        @Override
        public Object[] toArray() {
            return new Object[0];
        }

        @Override
        public <R> R[] toArray(R[] r) {
            Arrays.fill(r, null);
            return r;
        }

        @Override
        public String toString() {
            return "None";
        }

    }

    /**
     * Represents existing values of type <code>T</code>.
     */
    public static class Some<T> extends Option<T> {

        final T value;

        public Some(T value) {
            this.value = value;
        }

        @Override
        public boolean isDefined() {
            return true;
        }

        @Override
        public int size() {
            return 1;
        }

        @Override
        public T get() {
            return value;
        }

        public Iterator<T> iterator() {
            return Collections.singletonList(value).iterator();
        }

        @Override
        public boolean contains(Object o) {
            return o != null && o.equals(value);
        }

        @Override
        public boolean containsAll(Collection<?> c) {
            return (c.size() == 1) && (c.toArray()[0].equals(value));
        }

        @Override
        public Object[] toArray() {
            Object[] result = new Object[1];
            result[0] = value;
            return result;
        }

        @Override
        public <R> R[] toArray(R[] r) {
            if(r.length == 0){
                 R[] array = (R[])Arrays.copyOf(r, 1);
                 array[0] = (R)value;
                 return array;
            }else{
                 Arrays.fill(r, 1, r.length, null);
                 r[0] = (R)value;
                 return r;
            }
        }

        @Override
        public String toString() {
            return "Some(" + value + ")";
        }
    }

    /**
     * Represents a value of one of two possible types (a disjoint union)
     */
    public static class Either<A, B> {

        /**
         * The left value.
         */
        final public Option<A> left;

        /**
         * The right value.
         */
        final public Option<B> right;

        private Either(Option<A> left, Option<B> right) {
            this.left = left;
            this.right = right;
        }

        /**
         * Constructs a left side of the disjoint union, as opposed to the Right side.
         *
         * @param value The value of the left side
         * @return A left sided disjoint union
         */
        public static <A, B> Either<A, B> Left(A value) {
            return new Either<A, B>(Some(value), None());
        }

        /**
         * Constructs a right side of the disjoint union, as opposed to the Left side.
         *
         * @param value The value of the right side
         * @return A right sided disjoint union
         */
        public static <A, B> Either<A, B> Right(B value) {
            return new Either<A, B>(None(), Some(value));
        }

        @Override
        public String toString() {
            return "Either(left: " + left + ", right: " + right + ")";
        }
    }

    /**
     * A pair - a tuple of the types <code>A</code> and <code>B</code>.
     */
    public static class Tuple<A, B> {

        final public A _1;
        final public B _2;

        public Tuple(A _1, B _2) {
            this._1 = _1;
            this._2 = _2;
        }

        @Override
        public String toString() {
            return "Tuple2(_1: " + _1 + ", _2: " + _2 + ")";
        }
    }

    /**
     * Constructs a tuple of A,B
     *
     * @param a The a value
     * @param b The b value
     * @return The tuple
     */
    public static <A, B> Tuple<A, B> Tuple(A a, B b) {
        return new Tuple<A, B>(a, b);
    }

    /**
     * A tuple of A,B,C
     */
    public static class Tuple3<A, B, C> {

        final public A _1;
        final public B _2;
        final public C _3;

        public Tuple3(A _1, B _2, C _3) {
            this._1 = _1;
            this._2 = _2;
            this._3 = _3;
        }

        @Override
        public String toString() {
            return "Tuple3(_1: " + _1 + ", _2: " + _2 + ", _3:" + _3 + ")";
        }
    }

    /**
     * Constructs a tuple of A,B,C
     *
     * @param a The a value
     * @param b The b value
     * @param c The c value
     * @return The tuple
     */
    public static <A, B, C> Tuple3<A, B, C> Tuple3(A a, B b, C c) {
        return new Tuple3<A, B, C>(a, b, c);
    }

    /**
     * A tuple of A,B,C,D
     */
    public static class Tuple4<A, B, C, D> {

        final public A _1;
        final public B _2;
        final public C _3;
        final public D _4;

        public Tuple4(A _1, B _2, C _3, D _4) {
            this._1 = _1;
            this._2 = _2;
            this._3 = _3;
            this._4 = _4;
        }

        @Override
        public String toString() {
            return "Tuple4(_1: " + _1 + ", _2: " + _2 + ", _3:" + _3 + ", _4:" + _4 + ")";
        }
    }

    /**
     * Constructs a tuple of A,B,C,D
     *
     * @param a The a value
     * @param b The b value
     * @param c The c value
     * @param d The d value
     * @return The tuple
     */
    public static <A, B, C, D> Tuple4<A, B, C, D> Tuple4(A a, B b, C c, D d) {
        return new Tuple4<A, B, C, D>(a, b, c, d);
    }

    /**
     * A tuple of A,B,C,D,E
     */
    public static class Tuple5<A, B, C, D, E> {

        final public A _1;
        final public B _2;
        final public C _3;
        final public D _4;
        final public E _5;

        public Tuple5(A _1, B _2, C _3, D _4, E _5) {
            this._1 = _1;
            this._2 = _2;
            this._3 = _3;
            this._4 = _4;
            this._5 = _5;
        }

        @Override
        public String toString() {
            return "Tuple5(_1: " + _1 + ", _2: " + _2 + ", _3:" + _3 + ", _4:" + _4 + ", _5:" + _5 + ")";
        }
    }

    /**
     * Constructs a tuple of A,B,C,D,E
     *
     * @param a The a value
     * @param b The b value
     * @param c The c value
     * @param d The d value
     * @param e The e value
     * @return The tuple
     */
    public static <A, B, C, D, E> Tuple5<A, B, C, D, E> Tuple5(A a, B b, C c, D d, E e) {
        return new Tuple5<A, B, C, D, E>(a, b, c, d, e);
    }

}
