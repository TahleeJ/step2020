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

    if (nextSlide > slides.length) {
        slideIndices[slideSection] = 1;
    } else if (nextSlide < 1) {
        slideIndices[slideSection] = slides.length;  
    }

    for (i = 0; i < slides.length; i++) {
        slides[i].style.display = "none";

        if (slideSection > 0) {
            modals[i].style.display = "none";
        }

        if (slideSection == 0) {
            document.getElementById("major-container").style.display = "none";
        } 
    }

    slides[slideIndices[slideSection] - 1].style.display = "block";
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
    console.log(numC);
    console.log(document.getElementById("test").value);
    const response = await fetch('/data?num-comments='+numC);
    const comments = await response.json();
    const commentBox = document.getElementById("comment-box");
    commentBox.innerHTML = '';
        
    console.log(comments.length);

    comments.forEach((comment) => {
        commentBox.appendChild(createComment(comment));
        console.log(1);
    });

    return false;
}

function createComment(comment) {
    const p = document.createElement("p");
    p.className = "comments-p";
    p.innerHTML = comment.text;
    console.log(2);
    return p;
}

function deleteAll() {
    fetch('/delete-data', {method: 'POST'}).then(getComments());
}

function displaySankey() {
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
        height: 1200,
        position: "center",
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