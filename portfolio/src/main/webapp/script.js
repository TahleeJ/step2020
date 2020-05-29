var slideIndices = [1, 1, 1];
var slideSections = ["ed-slides", "project-slides", "work-slides"];

for (i = 0; i < 3; i++) {
    showSlides(slideIndices[i], i);
}

function plusSlides(n, o) {
    showSlides(slideIndices[o] += n, o);
}

function currentSlide(n) {
    for (i = 0; i < 3; i++) {
        showSlides(slideIndices[i] = n, i);
    }
}

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