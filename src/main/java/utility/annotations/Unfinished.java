package utility.annotations;

import java.lang.annotation.Documented;

/**
 * This annotation marks a class or method which is yet unfinished
 * and supposed to be expanded or otherwise worked on in the future.
 * <p>Example:
 * <pre>
 * <span style="color:lime">/**
 *  * This gets an object.
 *  *&#47;</span>
 * <span style="color:yellow">@Unfinished</span>
 * public Object getSomething() {
 *     return null; // TOD&#79;
 * }</pre>
 * @author Eric Lakhter
 */
@Documented
public @interface Unfinished {
}
