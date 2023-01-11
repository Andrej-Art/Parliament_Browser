package utility.annotations;

/**
 * This annotation marks a class or method which is solely used for testing,
 * going unused in the finished product.
 * <p>Example:
 * <pre>
 * <span style="color:lime">/**
 *  * This is to test X.
 *  *&#47;</span>
 * <span style="color:yellow">@Testing</span>
 * public static void main(String[] args) {
 *     // ... code goes here
 * }</pre>
 * @author Eric Lakhter
 */
public @interface Testing {
}
