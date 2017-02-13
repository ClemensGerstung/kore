package com.typingsolutions.kore;

import com.typingsolutions.kore.common.EventArgs;
import com.typingsolutions.kore.common.IEvent;
import org.hamcrest.core.Every;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
  @Test
  public void eventHandlingTest() {
    IEvent<Integer> event =  (sender, e) -> assertEquals(1000L, (long)e.getData());

    event.callback(this, new EventArgs<>(1000));
  }
}