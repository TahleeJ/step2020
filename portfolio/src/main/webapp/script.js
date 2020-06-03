var slideIndices = [1, 1, 1];
var slideSections = ["ed-slides", "project-slides", "work-slides"];

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
  if (nextSlide> slides.length) {slideIndices[slideSection] = 1}    
  if (nextSlide < 1) {slideIndices[slideSection] = slides.length}
  for (i = 0; i < slides.length; i++) {
      slides[i].style.display = "none";  
  }
    slides[slideIndices[slideSection]-1].style.display = "block";  
}

// async function getHelloMessage() {
//     const response = await fetch("/data");
//     const message = await response.text();
//     document.getElementById("message-container").innerText = message;
// }

function getComments() {
   fetch('/data').then(response => response.json()).then((comments) => {
    const commentBox = document.getElementById("comment-box");

    comments.forEach((comment) => {
        commentBox.appendChild(createComment(comment));
    })

    // for (i = 0; i < comments.length; i++) {
    //     commentBox.appendChild(createComment(comments[i]));
    // }
  });
}

function createComment(text) {
    const p = document.createElement("p");
    p.innerHTML = text;
    return p;
}
