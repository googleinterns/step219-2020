async function loadToDos() {
  fetch('/send-task').then(response => response.json()).then((tasksList) => {
    container = document.getElementById('task-container');
    console.log(tasksList);
    container.innerText = '';


    //Debug element
    container.appendChild(createListElement({
      place : {
        string : "place"
      },
      time : {
        date : "date"
      },
      taskText : {
        comment : "comment",
        title : "debug"
      }
    }))

    for (const task of tasksList) {
        container.appendChild(createListElement(task));
    }
  });
}

function findParentListView(event) {
  for (view of event.path)
    if (view.localName == "li") {
      return view;
    }
}

async function editFieldData(event) {
  console.log(event)
  
  taskView = findParentListView(event);

  elementView = event.path[0];
  askResult = prompt("Do you want to change this field?", elementView.innerText)
  if (askResult == null)
    return;
  
  elementView.innerText = askResult;

  requestParams = "field=" + elementView.className + "&type=edit&" + "new_data=" + askResult + "&number=" + taskView.id;
  await fetch('/remove-task', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: requestParams
  });

  await fetch('/send-task', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: requestParams
  });
}

function createTaskCommentElement(task) {
  const taskCommentElement = document.createElement("div");
  taskCommentElement.setAttribute("class", "task_commentData");
  taskCommentElement.addEventListener("click", editFieldData);
  taskCommentElement.innerText = task.taskText.comment;
  return taskCommentElement;
}

function createTaskTimeElement(task) {
  const taskTimeElement = document.createElement("div");
  taskTimeElement.setAttribute("class", "task_timeData");
  taskTimeElement.addEventListener("click", editFieldData);
  taskTimeElement.innerText = task.time.date;
  return taskTimeElement;
}

function createTaskTitleElement(task) {
  const taskTitleElement = document.createElement("div");
  taskTitleElement.setAttribute("class", "task_titleData");
  taskTitleElement.addEventListener("click", editFieldData);
  taskTitleElement.innerText = task.taskText.title;
  return taskTitleElement;
}

function createTaskPlaceElement(task) {
  const taskPlaceElement = document.createElement("div");
  taskPlaceElement.setAttribute("class", "task_placeData");
  taskPlaceElement.addEventListener("click", editFieldData);
  taskPlaceElement.innerText = task.place.string;
  return taskPlaceElement;
}

function createTaskDataholderElement(task) {
    const taskDataholderElement = document.createElement("div");
    taskDataholderElement.setAttribute("class", "task_dataholder");
    taskDataholderElement.appendChild(createTaskTitleElement(task));
    taskDataholderElement.appendChild(createTaskTimeElement(task));
    taskDataholderElement.appendChild(createTaskPlaceElement(task));
    return taskDataholderElement;
}

function getConfirmation() {
  result = confirm("Do you really want to remove this task?");
  return result;
}

async function removeElement(view) {
  notificationText = "type=notify&number=" + view.id;
  await fetch('/remove-task', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: notificationText
  });

  
  await fetch('/send-task', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: notificationText
  })
  
  view.remove();
}

function doRemoveEvent(event) {
  elementId = event.path[2].id;
  view = document.getElementById(elementId);
  
  view.setAttribute("class", "chosen_tasklist_node");
  result = getConfirmation();
  if (result) {
    removeElement(view);
  }
  view.setAttribute("class", "tasklist_node");
}

function createButtonElements() {
	const buttonHolder = document.createElement("div");
  buttonHolder.setAttribute("class", "task_buttonHolder");

  const removeButton = document.createElement("span")
  removeButton.innerText = "REMOVE";
  removeButton.setAttribute("class", "task_button")
  removeButton.addEventListener("click", doRemoveEvent);

  buttonHolder.appendChild(removeButton);
  return buttonHolder;
}

function createListElement(task) {
    const liElement = document.createElement("li");
    liElement.setAttribute("class", "tasklist_node");
    liElement.setAttribute("id", task.number);

    liElement.appendChild(createButtonElements());
    liElement.appendChild(createTaskDataholderElement(task));
    liElement.appendChild(createTaskCommentElement(task));
    return liElement;
}



function initMap() {
  const map = new google.maps.Map(
      document.getElementById('map'),
      {center: {lat: 55.752779, lng: 37.621588},
      zoom: 6,
      clickableIcons: true,
      backgroundColor: "#red"
    });
  console.log('map showed');
  getCurrentGeolocation(map);
  console.log('geolocation');
  var mapMarkers = new Object();
  var mapInfos = new Object();
  showTasksOnMap(map, mapMarkers, mapInfos);
}

async function showTasksOnMap(map, mapMarkers, mapInfos) {
    fetch('/send-task').then(response => response.json()).then((tasksList) => {
        for (const task of tasksList) {
            markerName = "lat" + task.place.lat.toString() + "lng" + task.place.lng.toString();

            if (markerName in mapMarkers) {
                addInfoContent(task, mapInfos[markerName]);
                mapMarkers[markerName].addListener('click', function() {
                    mapInfos[markerName].open(map, mapMarkers[markerName]);
                });
            } else {
                mapInfos[markerName] = new google.maps.InfoWindow({
                    content: ''});
                addInfoContent(task, mapInfos[markerName]);
                var pos = {lat: task.place.lat, lng: task.place.lng};
                mapMarkers[markerName] = new google.maps.Marker({
                    position: pos,
                    map: map,
                    title: task.place.string});
                mapMarkers[markerName].addListener('click', function() {
                    mapInfos[markerName].open(map, mapMarkers[markerName]);
                });
            }
    }
    });
}

function addInfoContent(task, infoW) {
    innerText = document.getElementById(task.number);
    infoW.setContent(infoW.getContent() + innerText.innerHTML.toString());
}

function calculateAndDisplayRoute(directionsService, directionsRenderer, from, to) {
  //from and to are google.maps.Marker class objects
  console.log("entered calculateAndD");
  const selectedMode = document.getElementById("mode").value;
  directionsService.route(
    {
      origin: {lat: from.position.lat(), lng: from.position.lng() },
      destination: {lat: to.lat(),
          lng: to.lng()},
      travelMode: google.maps.TravelMode[selectedMode]
    },
    (response, status) => {
      if (status == "OK") {
        directionsRenderer.setDirections(response);
      } else {
        window.alert("Directions request failed due to " + status);
      }
    }
  );
}

function getCurrentGeolocation(map) {
  infoWindow = new google.maps.InfoWindow();

  if (navigator.geolocation) {
    navigator.geolocation.getCurrentPosition(
      position => {
        const pos = {
          lat: position.coords.latitude,
          lng: position.coords.longitude
        };
        infoWindow.setPosition(pos);
        infoWindow.setContent("Location found.");
        infoWindow.open(map);
        map.setCenter(pos);
      },
      () => {
        handleLocationError(true, infoWindow, map.getCenter());
      }
    );
  } else {
    // Browser doesn't support Geolocation
    handleLocationError(false, infoWindow, map.getCenter());
  }
}

function handleLocationError(browserHasGeolocation, infoWindow, pos) {
  infoWindow.setPosition(pos);
  infoWindow.setContent(
    browserHasGeolocation
      ? "Error: The Geolocation service failed."
      : "Error: Your browser doesn't support geolocation."
  );
  infoWindow.open(map);
}

function showMap() {
    mapCurState = document.getElementById('map');
    taskForm = document.getElementById('task-form');
    taskContainer = document.getElementById('task-container');
    if (mapCurState.style.display == "none") {
        initMap();
        mapCurState.style.display = "block";
        taskForm.style.display = "none";
        taskContainer.style.display = "none";
    } else {
        mapCurState.style.display = "none";
        taskForm.style.display = "block";
        taskContainer.style.display = "block";
    }
}

