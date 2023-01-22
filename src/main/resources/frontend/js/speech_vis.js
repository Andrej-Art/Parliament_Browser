// Contains the applyDataToSpeech() function.

/**
 * Inserts an icon at the end of each sentence which shows a sentence's sentiment.<br>
 * Changes a words background colour in accordance to their Named Entity Category.
 * <ul>
 *     <li><tt>PER</tt> is red</li>
 *     <li><tt>ORG</tt> is blue</li>
 *     <li><tt>LOC</tt> is yellow</li>
 * </ul>
 * Inserts comments at their respective positions in the text.
 * @param text          Speech text.
 * @param sentenceData  Dataset from which to find the sentiment and sentence ends.
 * @param perData       Dataset from which to find the PER named entities and their positions.
 * @param orgData       Dataset from which to find the ORG entities and their positions.
 * @param locData       Dataset from which to find the LOC entities and their positions.
 * @param commentData   Dataset from which to find the comments and their positions.
 * @author Eric Lakhter
 */
function applyDataToSpeech(
    text = "Das hier ist ein Text.",
    sentenceData = [{endPos : 10, sentiment : 0.0}],
    perData = [{startPos: 2, endPos: 3}],
    orgData = [{startPos: 4, endPos: 5}],
    locData = [{startPos: 6, endPos: 7}],
    commentData = [{
        full_name: "Bob Baumeister", text: "(Heiterkeit)", commentPos: 10, party: "SPD", sentiment: 0.1, commentator_id: "0001"
    }]
) {
    let speechArray = text.split('');
    let sentenceIndex = 0;
    let perIndex = 0;
    let orgIndex = 0;
    let locIndex = 0;
    let commentIndex = 0;
    let finalSpeech = "";
    for (let i = 0; i < speechArray.length; i++) {

        // insert icon at end of sentence
        if (sentenceIndex < sentenceData.length && i === sentenceData[sentenceIndex]["endPos"]) {
            let sentiment = sentenceData[sentenceIndex]["sentiment"];
            if (sentiment > 0) {
                finalSpeech += '🤔<span style="color: blue">' + sentiment + '</span>';
            } else if (sentiment === 0) {
                finalSpeech += '🤔<span style="color: orange">' + sentiment + '</span>';
            } else {
                finalSpeech += '🤔<span style="color: red">' + sentiment + '</span>';
            }
            sentenceIndex++;
        }

        // mark named entities: PER, ORG, LOC data sets
        if (perIndex < perData.length) {
            if (i === perData[perIndex]["startPos"]) {
                finalSpeech += '<span style="background-color: tomato">';
            } else if (i === perData[perIndex]["endPos"]) {
                finalSpeech += '</span>';
                perIndex++;
            }
        }
        if (orgIndex < orgData.length) {
            if (i === orgData[orgIndex]["startPos"]) {
                finalSpeech += '<span style="background-color: aqua">';
            } else if (i === orgData[orgIndex]["endPos"]) {
                finalSpeech += '</span>';
                orgIndex++;
            }
        }
        if (locIndex < locData.length) {
            if (i === locData[locIndex]["startPos"]) {
                finalSpeech += '<span style="background-color: yellow">';
            } else if (i === locData[locIndex]["endPos"]) {
                finalSpeech += '</span>';
                locIndex++;
            }
        }

        // insert comments at their respective position
        if (commentIndex < commentData.length && i === commentData[commentIndex]["commentPos"]) {
            finalSpeech +=
                ('<br><span style="color: darkslategray">'
                + commentData[commentIndex]["full_name"]
                + '[' + commentData[commentIndex]["party"] + ']: '
                +  commentData[commentIndex]["text"] + '</span><br>');
            commentIndex++;
        }

        // Build up the result String after each step
        finalSpeech += speechArray[i];
    }
    return finalSpeech;
}
