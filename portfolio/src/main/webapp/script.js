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
    }
    if (nextSlide < 1) {
        slideIndices[slideSection] = slides.length;  
    }

    for (i = 0; i < slides.length; i++) {
        slides[i].style.display = "none";

        if (slideSection > 0) {
            modals[i].style.display = "none";
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
    document.getElementById("main").style.display = "none";
    document.getElementById("comment-container").style.width = "40%"
    document.getElementById("comment-container").style.display = "block";
}

function closeComments() {
    document.getElementById("comment-container").style.width = "0";
    document.getElementById("comment-container").style.display = "none";
    document.getElementById("main").style.display = "block";
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

    return false;
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