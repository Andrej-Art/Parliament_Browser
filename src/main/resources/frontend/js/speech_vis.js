
/**
 * Inserts an icon at the end of each sentence which shows a sentence's sentiment.
 * @param tagID ID of the element to insert the sentiments in.
 * @param data Dataset from which to find the sentiment and sentence ends.
 * @author Eric Lakhter
 */
function applySentenceSentiment(
    tagID = "speech",
    data = [{startPos : 0, endPos : 1, sentiment : 0.0}]
) {
    for (let i in data) {
        console.log(data[i]);
    }
    let test = data;
}

/**
 * Changes a words background colour in accordance to their Named Entity Category.
 * <ul>
 *     <li><tt>PER</tt> is red</li>
 *     <li><tt>ORG</tt> is blue</li>
 *     <li><tt>LOC</tt> is yellow</li>
 * </ul>
 * @param tagID ID of the element to insert the sentiments in.
 * @param data Dataset from which to find the named entities and their positions.
 * @author Eric Lakhter
 */
function applyNamedEntitiesMarkers(
    tagID = "speech",
    data = [{coveredText: "", startPos: 0, endPos: 1}]
) {
    let test = data;
}

/**
 *
 * @param tagID ID of the element to insert the sentiments in.
 * @param data Dataset from which to find the comments and their positions.
 */
function insertComments(
    tagID = "speech",
    data = [{
        full_name: "Bob Baumeister", text: "(Heiterkeit)", party: "SPD", sentiment: 0.1,
        speaker_id: "0002", commentator_id: "0001", first_name: "Bob", last_name: "Baumeister", _id: "0001", speech_id: "ID19100100",
    }]
) {
    let test = data;
}
