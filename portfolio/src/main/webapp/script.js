// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

var slideIndices = [1, 1, 1];
var slideSections = ["ed-slides", "project-slides", "work-slides"];
var slidePics = ["ed-pics", "project-pics", "work-pics"];

var modalBoxes = ["modal-1", "modal-2"];
var modalImages = ["modal-content-1", "modal-content-2"];
var closeButtons = ["proj-close", "work-close"];

for (i = 0; i < 3; i++) {
    showSlides(slideIndices[i], i);
}

// nextSlide represents whether the slide will switch to the next or previous slide
// slideSection represents which section of slides is receving the change as referenced by "slideSections"
function plusSlides(nextSlide, slideSection) {
    slideIndices[slideSection] += nextSlide;
    showSlides(slideIndices[slideSection], slideSection);
}

// nextSlide represents whether the slide will switch to the next or previous slide
// slideSection represents which section of slides is receving the change as referenced by "slideSections"
function showSlides(nextSlide, slideSection) {
    var i;
    var slides = document.getElementsByClassName(slideSections[slideSection]);

    if (slideSection > 0) {
        modals = document.getElementsByClassName(modalBoxes[slideSection - 1]);
    }

    // The slides are meant to exist on a 1 to (slides.length) basis instead of a 0 to (slides.length - 1) basis because 
    //      1 will be subtracted when it is time to set the display of the slide (first slide second slide instead of slide at index 0 etc.)
    // If nextSlide is greater than the number of slides, the current index of this section will be set to the first slide
    // If nextSlide is 0, the current index of this section will be set to the last slide
    // If nextSlide is positive, the current slide will be set to the previous or next slide as done in the plusSlides function
    if (nextSlide > slides.length) {
        slideIndices[slideSection] = 1;
    } else if (nextSlide < 1) {
        slideIndices[slideSection] = slides.length;  
    }

    for (i = 0; i < slides.length; i++) {
        slides[i].style.display = "none";

        // If slideSection is 0, the container holding the Sankey diagram will be hidden until opened through the displaySankey function
        // If the slideSection is not 0, the modals containing the enlarged versions of the displayed images will be hidden until the image is clicked on
        if (slideSection == 0) {
            document.getElementById("major-container").style.display = "none";
        } else {
            modals[i].style.display = "none";
        }       
    }

    slides[slideIndices[slideSection] - 1].style.display = "flex";
    slides[slideIndices[slideSection] - 1].style.width = "100%";
}

function displayModal(modalb, modali, img, slide) {
    document.getElementsByClassName(modalBoxes[modalb])[slide].style.display = "block";
    document.getElementsByClassName(modalImages[modali])[slide].src = document.getElementsByClassName(slidePics[img])[slide].src;
    
}

function closeModal(modalb, slide) {
    document.getElementsByClassName(modalBoxes[modalb])[slide].style.display = "none";
}

function openComments() {
    document.getElementById("comment-open").style.display = "none";
    document.getElementById("comment-container").style.width = "645px";
    document.getElementById("comment-container").style.display = "block";
}

function closeComments() {
    document.getElementById("comment-container").style.width = "0";
    document.getElementById("comment-container").style.display = "none";
    document.getElementById("comment-open").style.display = "block";
}

async function getComments() {
    var numC = document.getElementById("numComments").value;
    const response = await fetch('/data?num-comments='+numC);
    const comments = await response.json();
    const commentBox = document.getElementById("comment-box");
    commentBox.innerHTML = '';

    comments.forEach((comment) => {
        commentBox.appendChild(createComment(comment));
    });
}

function createComment(comment) {
    const p = document.createElement("p");
    p.className = "comments-p";
    p.innerHTML = comment.text;
    return p;
}

function deleteAll() {
    fetch('/delete-data', {method: 'POST'}).then(getComments());
}

function displaySankey() {
    console.log("click");
    document.getElementById("sankey-open").style.display = "none";
    document.getElementById("major-container").style.display = "block";
}

function closeSankey() {
    document.getElementById("major-container").style.display = "none";
    document.getElementById("sankey-open").style.display = "block";
}

google.charts.load("current", {packages:["sankey"]});
google.charts.setOnLoadCallback(drawChart)
function drawChart() {
    var data = new google.visualization.DataTable();
    data.addColumn('string', 'From');
    data.addColumn('string', 'To');
    data.addColumn('number', 'Weight');
    data.addColumn({type:'string', role:'tooltip'});
    data.addRows([
        [ 'Georgia Tech', 'Start College of Computing', 1, 'Percentage of Students: 9.0%' ],
        [ 'Georgia Tech', 'Start College of Design', 1, 'Percentage of Students: 4.2%' ],
        [ 'Georgia Tech', 'Start College of Engineering', 6, 'Percentage of Students: 63.6%' ],
        [ 'Georgia Tech', 'Start College of Sciences', 1, 'Percentage of Students: 11.5%' ],
        [ 'Georgia Tech', 'Start Ivan Allen College', 1, ' Percentage of Students: 6%' ],
        [ 'Georgia Tech', 'Start Scheller College of Business', 1, 'Percentage of Students: 5.7%' ],
        [ 'Start College of Computing', 'End College of Computing', 7, 'Percentage of Students: 77.1%' ],
        [ 'Start College of Computing', 'End College of Design', 1, 'Percentage of Students: 0.6%' ],
        [ 'Start College of Computing', 'End College of Engineering', 1, 'Percentage of Students: 8.6%' ],
        [ 'Start College of Computing', 'End College of Sciences', 1, 'Percentage of Students: 2.1%' ],
        [ 'Start College of Computing', 'End Ivan Allen College', 1, 'Percentage of Students: 4.8%' ],
        [ 'Start College of Computing', 'End Scheller College of Business', 1, 'Percentage of Students: 6.8%' ],
        [ 'Start College of Design', 'End College of Computing', 1, 'Percentage of Students: 2.2%' ],
        [ 'Start College of Design', 'End College of Design', 7, 'Percentage of Students: 70.3%' ],
        [ 'Start College of Design', 'End College of Engineering', 1, 'Percentage of Students: 11.0%' ],
        [ 'Start College of Design', 'End College of Sciences', 1, 'Percentage of Students: 2.1%' ],
        [ 'Start College of Design', 'End Ivan Allen College', 1, 'Percentage of Students: 4.0%' ],
        [ 'Start College of Design', 'End Scheller College of Business', 1, 'Percentage of Students: 10.4%' ],
        [ 'Start College of Engineering', 'End College of Computing', 1, 'Percentage of Students: 5.5%' ],
        [ 'Start College of Engineering', 'End College of Design', 1, 'Percentage of Students: 1.3%' ],
        [ 'Start College of Engineering', 'End College of Engineering', 8, 'Percentage of Students: 79.5%' ],
        [ 'Start College of Engineering', 'End College of Sciences', 1, 'Percentage of Students: 3.8%' ],
        [ 'Start College of Engineering', 'End Ivan Allen College', 1, 'Percentage of Students: 3.0%' ],
        [ 'Start College of Engineering', 'End Scheller College of Business', 1, 'Percentage of Students: 6.9%' ],
        [ 'Start College of Sciences', 'End College of Computing', 1, 'Percentage of Students: 5.5%' ],
        [ 'Start College of Sciences', 'End College of Design', 1, 'Percentage of Students: 1.2%' ],
        [ 'Start College of Sciences', 'End College of Engineering', 2, 'Percentage of Students: 23.6%' ],
        [ 'Start College of Sciences', 'End College of Sciences', 5, 'Percentage of Students: 49.7%' ],
        [ 'Start College of Sciences', 'End Ivan Allen College', 1, 'Percentage of Students: 7.5%' ],
        [ 'Start College of Sciences', 'End Scheller College of Business', 1, 'Percentage of Students: 12.6%' ],
        [ 'Start Ivan Allen College', 'End College of Computing', 1, 'Percentage of Students: 8.7%'],
        [ 'Start Ivan Allen College', 'End College of Design', 1, 'Percentage of Students: 1.5%' ],
        [ 'Start Ivan Allen College', 'End College of Engineering', 1, 'Percentage of Students: 8.8%' ],
        [ 'Start Ivan Allen College', 'End College of Sciences', 1, 'Percentage of Students: 3.2%' ],
        [ 'Start Ivan Allen College', 'End Ivan Allen College', 5, 'Percentage of Students: 56.0%' ],
        [ 'Start Ivan Allen College', 'End Scheller College of Business', 2, 'Percentage of Students: 21.8%'],
        [ 'Start Scheller College of Business', 'End College of Computing', 1, 'Percentage of Students: 1.1%'],
        [ 'Start Scheller College of Business', 'End College of Design', 1, 'Percentage of Students: 0.7%'],
        [ 'Start Scheller College of Business', 'End College of Engineering', 1, 'Percentage of Students: 6.6%' ],
        [ 'Start Scheller College of Business', 'End College of Sciences', 1, 'Percentage of Students: 1.0%' ],
        [ 'Start Scheller College of Business', 'End Ivan Allen College', 1, 'Percentage of Students: 3.6%' ],
        [ 'Start Scheller College of Business', 'End Scheller College of Business', 9, 'Percentage of Students: 86.9%' ],
    ]);

    var options = {
        width: 2400,
        height: 900,
        sankey: {
        node: { label: { color: "#ffffff",
                         fontSize: 30,
                         bold: true },
                nodePadding: 50 },
        link: { colorMode: 'gradient' } }
    };

    var chart = new google.visualization.Sankey(document.getElementById('sankey_multiple'));
    chart.draw(data, options);
}