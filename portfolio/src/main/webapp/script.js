var slideIndices = [1, 1, 1];
var slideSections = ["ed-slides", "project-slides", "work-slides"];

for (i = 0; i < 3; i++) {
    showSlides(slideIndices[i], i);
}

// n represents whether the slide will switch to the next or previous slide
// o represents which section of slides is receving the change as referenced by "slideSections"
function plusSlides(n, o) {
    slideIndices[o] += n;
    showSlides(slideIndices[o], o);
}

// n represents whether the slide will switch to the next or previous slide
// o represents which section of slides is receving the change as referenced by "slideSections"
function showSlides(n, o) {
  var i;
  var slides = document.getElementsByClassName(slideSections[o]);
  if (n > slides.length) {slideIndices[o] = 1}    
  if (n < 1) {slideIndices[o] = slides.length}
  for (i = 0; i < slides.length; i++) {
      slides[i].style.display = "none";  
  }
  slides[slideIndices[o]-1].style.display = "block";  
}