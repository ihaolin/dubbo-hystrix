package me.hao0.dubbo.hystrix;

import me.hao0.dubbo.hystrix.command.HelloWorldCommand;
import org.junit.Test;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;

import java.util.concurrent.Future;

import static org.junit.Assert.*;

/**
 * Author: haolin
 * Email: haolin.h0@gmail.com
 */
public class HelloWorldCommandTest {

    @Test
    public void testSynchronous() {
        assertEquals("Hello World!", new HelloWorldCommand("World").execute());
        assertEquals("Hello Bob!", new HelloWorldCommand("Bob").execute());
    }

    @Test
    public void testAsynchronous1() throws Exception {
        assertEquals("Hello World!", new HelloWorldCommand("World").queue().get());
        assertEquals("Hello Bob!", new HelloWorldCommand("Bob").queue().get());
    }

    @Test
    public void testAsynchronous2() throws Exception {

        Future<String> fWorld = new HelloWorldCommand("World").queue();
        Future<String> fBob = new HelloWorldCommand("Bob").queue();

        assertEquals("Hello World!", fWorld.get());
        assertEquals("Hello Bob!", fBob.get());
    }

    @Test
    public void testObservable() throws Exception {

        Observable<String> fWorld = new HelloWorldCommand("World").observe();
        Observable<String> fBob = new HelloWorldCommand("Bob").observe();

        // blocking
        assertEquals("Hello World!", fWorld.toBlocking().single());
        assertEquals("Hello Bob!", fBob.toBlocking().single());

        // non-blocking
        // - this is a verbose anonymous inner-class approach and doesn't do assertions
        fWorld.subscribe(new Observer<String>() {

            @Override
            public void onCompleted() {
                // nothing needed here
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(String v) {
                System.out.println("onNext: " + v);
            }

        });

        // non-blocking
        // - also verbose anonymous inner-class
        // - ignore errors and onCompleted signal
        fBob.subscribe(new Action1<String>() {

            @Override
            public void call(String v) {
                System.out.println("onNext: " + v);
            }

        });

        // non-blocking
        // - using closures in Java 8 would look like this:

        //            fWorld.subscribe((v) -> {
        //                System.out.println("onNext: " + v);
        //            })

        // - or while also including error handling

        //            fWorld.subscribe((v) -> {
        //                System.out.println("onNext: " + v);
        //            }, (exception) -> {
        //                exception.printStackTrace();
        //            })

        // More information about Observable can be found at https://github.com/Netflix/RxJava/wiki/How-To-Use

    }

}
