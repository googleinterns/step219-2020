async function add() {
  fetch('/Task').then(response => response.json()).then((jsonStr) => {
    // stats is an object, not a string, so we have to
    // reference its fields to create HTML content
    var temp = document.querySelector("#task");
    var task = temp.content.cloneNode(true);
    task.querySelector(".title").innerText = defaultName; //return from java
    var when = task.querySelector(".when");
    when.datetime = defaultDate; ////return from java
    when.innerText = defaultDateText;//return from java
    task.querySelector(".comment").innerText = defaultComment;//return from java
    task.querySelector(".adress").innerText = defaultAdress; //return from java
    document.body.appendChild(task);
  });
}