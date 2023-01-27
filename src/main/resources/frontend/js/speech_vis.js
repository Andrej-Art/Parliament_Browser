// Contains the applyDataToSpeech() function.
//
// It basically splits the complete text string into singular symbols (e.g. "butter" becomes ["b", "u", "t", "t", "e", "r"])
// and then advances step by step, inserting data where it needs to.

/**
 * Changes a words background colour in accordance to their Named Entity Category.
 * <ul>
 *     <li><tt>PER</tt> is red</li>
 *     <li><tt>ORG</tt> is blue</li>
 *     <li><tt>LOC</tt> is green</li>
 * </ul>
 * Inserts an icon at the end of each sentence which shows a sentence's sentiment.<br>
 * Inserts comments at their respective positions in the text.
 * @param text          Speech text.
 * @param perData       Dataset from which to find the PER named entities and their positions.
 * @param orgData       Dataset from which to find the ORG entities and their positions.
 * @param locData       Dataset from which to find the LOC entities and their positions.
 * @param sentenceData  Dataset from which to find the sentiment and sentence ends.
 * @param commentData   Dataset from which to find the comments and their positions.
 * @return Text to be inserted in a text element's <tt>innerHTML</tt>.
 * @author Eric Lakhter
 */
function applyDataToSpeech(
    text = "Das hier ist ein Text.",
    perData = [{startPos: 2, endPos: 3}],
    orgData = [{startPos: 4, endPos: 5}],
    locData = [{startPos: 6, endPos: 7}],
    sentenceData = [{endPos : 22, sentiment : 0.0}],
    commentData = [{text: "Heiterkeit", commentPos: 22}]
) {
    let speechArray = text.split('');
    // artificially increase text array length by one so the final sentence doesn't get cut off
    speechArray.push('');
    let perIndex = 0;
    let orgIndex = 0;
    let locIndex = 0;
    let sentenceIndex = 0;
    let commentIndex = 0;
    let finalSpeech = "";
    for (let i = 0; i < speechArray.length; i++) {

        // mark named entities: PER, ORG, LOC data sets
        if (perIndex < perData.length) {
            if (i === perData[perIndex]["startPos"]) {
                finalSpeech += '<span style="background-color: coral">';
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
                finalSpeech += '<span style="background-color: lime">';
            } else if (i === locData[locIndex]["endPos"]) {
                finalSpeech += '</span>';
                locIndex++;
            }
        }

        // insert icon at end of sentence
        if (sentenceIndex < sentenceData.length && i === sentenceData[sentenceIndex]["endPos"]) {
            finalSpeech += generateSentimentBlob(sentenceData[sentenceIndex]["sentiment"])
            sentenceIndex++;
        }

        // insert comments at their respective position
        if (commentIndex < commentData.length && i === commentData[commentIndex]["commentPos"]) {
            finalSpeech += formatCommentData(commentData[commentIndex]);
            commentIndex++;
        }

        // Build up the result String after each step
        finalSpeech += speechArray[i];
    }
    return finalSpeech;
}

/**
 * Inserts a blob which displays a sentiment value on mouse hover.
 * @param sentiment the value to be displayed.
 * @return String to be inserted in the speech's inner HTML.
 */
function generateSentimentBlob(sentiment = 0.0) {
    let returnSentiment = '';
    if (sentiment > 0) {
        returnSentiment += '<g class="sentiment">❔<span style="color: blue" class="hoverText">' + sentiment + '</g></span>';
    } else if (sentiment === 0) {
        returnSentiment += '<g class="sentiment">❔<span style="color: orange" class="hoverText">' + sentiment + '</g></span>';
    } else {
        returnSentiment += '<g class="sentiment">❔<span style="color: red" class="hoverText">' + sentiment + '</g></span>';
    }
    return returnSentiment;
}

/**
 * Formats given comment data and returns a String.
 * @param commentDatum Comment data to format.
 * @return Formatted String which is to be inserted in the displayed speech.
 */
function formatCommentData(commentDatum = {text: "Heiterkeit", commentPos: 22}) {
    let returnText = '';
    let fullText = commentDatum["text"];
    // if the comment has more than one part it gets split into multiple lines
    // example: "Beifall bei der AfD – Dr. Marco Buschmann [FDP]: Traditionen wollten Sie doch direkt brechen!"
    // =>
    // "Beifall bei der AfD"
    // "Dr. Marco Buschmann [FDP]: Traditionen wollten Sie doch direkt brechen!"
    if (fullText.includes("–")) {
        let textParts = fullText.split("–");
        for (let i in textParts) {
            if (commentDatum.hasOwnProperty("CommentatorData")
                && textParts[i].includes(commentDatum["CommentatorData"]["fullName"])) {
                returnText += ('<br>' +
                    '<img alt="Profilbild" src="' + commentDatum["CommentatorData"]["picture"][0] + '" style="width:60px;height:50px;"> ' +
                    '<span style="color: darkgreen">' + textParts[i] + '</span>');
            } else {
                returnText += ('<br><span style="color: darkgreen">' + textParts[i] + '</span>');
            }
        }
        returnText += '<br>';
    } else {
        if (commentDatum.hasOwnProperty("CommentatorData")
            && fullText.includes(commentDatum["CommentatorData"]["fullName"])) {
            returnText += ('<br>' +
                '<img alt="Profilbild" src="' + commentDatum["CommentatorData"]["picture"][0] + '" style="width:60px;height:50px;"> ' +
                '<span style="color: darkgreen">' + fullText + '</span><br>');
        } else {
            returnText += ('<br><span style="color: darkgreen">' + fullText + '</span><br>');
        }
    }
    return returnText;
}