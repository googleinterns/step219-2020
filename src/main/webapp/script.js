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
        time : "time"
      },
      date : {
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
  mapPanel = document.getElementById('floating-panel');
  mapPanel.style.display = "none";
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

function createTaskFeatureElement(taskFeature, feature) {
  const taskElement = document.createElement("div");
  taskElement.setAttribute("class", "task_"+feature+"Data");
  taskElement.addEventListener("click", editFieldData);
  taskElement.innerText = taskFeature;
  return taskElement;
}


function createTaskDataholderElement(task) {
    const taskDataholderElement = document.createElement("div");
    taskDataholderElement.setAttribute("class", "task_dataholder");
    taskDataholderElement.appendChild(createTaskFeatureElement(task.taskText.title, "title"));
    taskDataholderElement.appendChild(createTaskFeatureElement(task.time.time, "time"));
    taskDataholderElement.appendChild(createTaskFeatureElement(task.date.date, "date"));
    taskDataholderElement.appendChild(createTaskFeatureElement(task.place.string, "place"));
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
  liElement.appendChild(createTaskFeatureElement(task.taskText.comment, "comment"));
  return liElement;
}



function initMap() {
  const map = new google.maps.Map(
      document.getElementById('map'), {
        center: {lat: 55.752779, lng: 37.621588},
        zoom: 6,
        clickableIcons: true,
        backgroundColor: "#red"
      }
    );
  console.log('map showed');
  getCurrentGeolocation(map);
  var mapMarkers = new Object();
  var mapInfos = new Object();
  fetchTasksOnMap(map, mapMarkers, mapInfos);
  addDirections(map, mapMarkers);
}

function addDirections(map, mapMarkers) {
  const directionsRenderer = new google.maps.DirectionsRenderer();
  const directionsService = new google.maps.DirectionsService();
  directionsRenderer.setMap(map);
  directionsBetweenMarkers(mapMarkers, directionsService, directionsRenderer);
  document.getElementById("mode").addEventListener("change", () => {
    directionsBetweenMarkers(mapMarkers, directionsService, directionsRenderer);
  });
}

function directionsBetweenMarkers(mapMarkers, directionsService, directionsRenderer) {
  for (var marker1 in mapMarkers) {
      for (var marker2 in mapMarkers) {
          if (marker1 != marker2) {
              calculateAndDisplayRoute(directionsService, directionsRenderer, mapMarkers[marker1].position, mapMarkers[marker2].position);
          }
      }
  }
}


async function fetchTasksOnMap(map, mapMarkers, mapInfos) {
  const response = await fetch('/send-task');
  const tasksList = await response.json();
  showTasksOnMap(tasksList, map, mapMarkers, mapInfos);
}

function showTasksOnMap(tasksList, map, mapMarkers, mapInfos) {
  for (const task of tasksList) {
      markerName = `lat${task.place.lat}lng${task.place.lng}`;
      if (markerName in mapMarkers) {
          mapInfos[markerName].setContent(mapInfos[markerName].getContent() + composeNewInfoContent(task.number));
      } else {
          mapInfos[markerName] = new google.maps.InfoWindow({
              content: ''});
          mapInfos[markerName].setContent(composeNewInfoContent(task.number));
          mapMarkers[markerName] = new google.maps.Marker({
              position: task.place,
              map: map,
              title: markerName,//task.place.string
              icon: {url: "https://maps.google.com/mapfiles/ms/icons/blue-dot.png"}});
          mapMarkers[markerName].setIcon(composeIconUrl(task.time));
      }
  }
  for (var markerName in mapMarkers) {
    //explanation of that code is here https://leewc.com/articles/google-maps-infowindow/
    //there was a problem with handling several info windows
  mapMarkers[markerName].infowindow = mapInfos[markerName];
  mapMarkers[markerName].addListener('click', function() {
	  return this.infowindow.open(map, this);
  });
  google.maps.event.addListener(mapMarkers[markerName], 'click', function() {
	  this.infowindow.open(map, this); 
  });
  }
  console.log(mapMarkers);
  console.log(mapInfos);
}

function composeIconUrl(task_time) {
  //change icon color depend on  time
  let urls = "https://maps.google.com/mapfiles/ms/icons/";
  var color ="";
  if (task_time < '2') {
      color = "red";
  } else if (task_time < "3") {
      color = "green";
  } else if (task_time < "4") {
      color = "orange";
  } else if (task_time < "5") {
      color = "yellow";
  } else {
      color = "purple";
  }
  urls += color + "-dot.png";
  return urls;
}

function composeNewInfoContent(task_number) {
  //git innerHTML to add it task to infowindow content
  let taskElement = document.getElementById(task_number);
  return taskElement.innerHTML;
}

function calculateAndDisplayRoute(directionsService, directionsRenderer, from_pos, to_pos) {
  //put direction on the map
  const selectedMode = document.getElementById("mode").value;
  directionsService.route(
    {
      origin: from_pos,
      destination: to_pos,
      travelMode: google.maps.TravelMode[selectedMode]//,
      //i will need this in next commit
      /*transitOptions: {
          departureTime: Date,
      }*/
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
        handleLocationError('browser has geolocation', infoWindow, map.getCenter());
      }
    );
  } else {
    // Browser doesn't support Geolocation
    handleLocationError('browser does not have geolocation', infoWindow, map.getCenter());
  }
}

function handleLocationError(browserGeoState, infoWindow, pos) {
  var browserHasGeolocation = true;
  if (browserGeoState == 'browser does not have geolocation') {
      browserHasGeolocation = false;
  }
  infoWindow.setPosition(pos);
  infoWindow.setContent(
    browserHasGeolocation
      ? "Error: The Geolocation service failed."
      : "Error: Your browser doesn't support geolocation."
  );
  infoWindow.open(map);
}

function showHideMap() {
  mapCurState = document.getElementById('map');
  mapPanel = document.getElementById('floating-panel');
  taskForm = document.getElementById('task-form');
  taskContainer = document.getElementById('task-container');
  if (mapCurState.style.display == "none") {
      initMap();
      mapCurState.style.display = "block";
      mapPanel.style.display = "block";
      taskForm.style.display = "none";
      taskContainer.style.display = "none";
  } else {
      mapCurState.style.display = "none";
      mapPanel.style.display = "none";
      taskForm.style.display = "block";
      taskContainer.style.display = "block";
    }
}

