/** Element which is chosen by user for now */
let toggledElement = null;

/** Loads list of user tasks from server and puts it into view*/
async function loadToDos() {
  const response = await fetch('/update-local-task-list');
  const tasksList = await response.json();

  const container = document.getElementById('task-container');
  console.log(tasksList);
  container.innerText = '';
  //Debug element
  container.appendChild(createListElement({
    place: {
      string: "place"
    },
    dateTime: {
      calendarDate: "Jan 1, 2020 12:00:00 AM"
    },
    comment: "comment",
    title: "debug"
  }))

  for (const task of tasksList) {
    container.appendChild(createListElement(task));
  }

  mapPanel = document.getElementById('floating-panel');
  mapPanel.style.display = "none";
}

/** Finds a container with task data where current event was called */
function findParentListView(event) {
  for (let view of event.path) {
    if (view.localName === "li") {
      return view;
    }
  }
}

async function editFieldData(event) {
  console.log(event)

  const taskView = findParentListView(event);

  if (taskView !== toggledElement) {
    return;
  }

  const elementView = event.path[0];
  const askResult = prompt("Do you want to change this field?",
      elementView.innerText)
  if (askResult == null) {
    return;
  }
  elementView.innerText = askResult;
  const requestParams = "field=" + elementView.className + "&type=edit&"
      + "new_data=" + askResult + "&number=" + taskView.id;

  const req1 = fetchHelper("/update-server-task-list", requestParams);
  const req2 = fetchHelper("/update-local-task-list", requestParams);

  await req2;
  await req1;
}

function createTaskCommentElement(task) {
  const taskCommentElement = document.createElement("input");
  taskCommentElement.setAttribute("class", "task_commentData");
  taskCommentElement.setAttribute("id", "comment" + task.datastoreId);
  //taskCommentElement.addEventListener("click", editFieldData);
  taskCommentElement.setAttribute("readonly", "readonly");
  taskCommentElement.setAttribute("value", task.comment);
  return taskCommentElement;
}

function createTaskTimeElement(task) {
  const taskTimeElement = document.createElement("input");
  taskTimeElement.setAttribute("class", "task_timeData");
  taskTimeElement.setAttribute("id", "time" + task.datastoreId);
  taskTimeElement.setAttribute("readonly", "readonly");
  //taskTimeElement.addEventListener("click", editFieldData);
  taskTimeElement.setAttribute("value", task.time.date);
  return taskTimeElement;
}

function createTaskTitleElement(task) {
  const taskTitleElement = document.createElement("input");
  taskTitleElement.setAttribute("class", "task_titleData");
  taskTitleElement.setAttribute("id", "title" + task.datastoreId);
  taskTitleElement.setAttribute("readonly", "readonly");
  //taskTitleElement.addEventListener("click", editFieldData);
  taskTitleElement.setAttribute("value", task.title);

  return taskTitleElement;
}

function createTaskPlaceElement(task) {
  const taskPlaceElement = document.createElement("input");
  taskPlaceElement.setAttribute("class", "task_placeData");
  taskPlaceElement.setAttribute("id", "place" + task.datastoreId);
  taskPlaceElement.setAttribute("readonly", "readonly");
  //taskPlaceElement.addEventListener("click", editFieldData);
  taskPlaceElement.setAttribute("value", task.place.name);
  return taskPlaceElement;
}

function getConfirmation() {
  return confirm("Do you really want to remove this task?");
}

async function fetchHelper(servletName, requestBody) {
  return fetch(servletName, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: requestBody
  });
}

/** Removes task which is connected with this view */
async function removeElement(view) {
  if (view === toggledElement) {
    untoggleElement();
  }

  const notificationText = "type=delete&number=" + view.id;
  const req1 = fetchHelper("/update-server-task-list", notificationText);
  const req2 = fetchHelper("/update-local-task-list", notificationText);

  await req1;
  await req2;

  view.remove();
}

function doRemoveEvent(event) {
  const view = findParentListView(event);
  const result = getConfirmation();
  if (result) {
    removeElement(view);
  }
}

function createButtonElements(task) {
  const buttonHolder = document.createElement("div");
  buttonHolder.setAttribute("class", "task_buttonHolder");

  const removeButton = document.createElement("img")
  removeButton.setAttribute("class", "task_button")
  removeButton.addEventListener("click", doRemoveEvent);
  removeButton.setAttribute("src", "./images/clear-48dp.svg");
  removeButton.setAttribute("id", "remove_btn" + task.datastoreId);
  removeButton.setAttribute("hidden", "hidden");

  buttonHolder.appendChild(removeButton);
  return buttonHolder;
}

function buildTaskRightPanel(task) {
  const taskRightPanel = document.createElement("div");

  taskRightPanel.setAttribute("class", "task_rightPanel");

  taskRightPanel.appendChild(createButtonElements(task));
  taskRightPanel.appendChild(createTaskTimeElement(task));
  taskRightPanel.appendChild(createTaskPlaceElement(task));
  return taskRightPanel;
}

function buildMainTaskDataPanel(task) {
  const mainTaskPanel = document.createElement("div");
  mainTaskPanel.appendChild(createTaskTitleElement(task));
  mainTaskPanel.appendChild(createTaskCommentElement(task));
  return mainTaskPanel;
}

/** Changes the state of given element to toggled */
function toggleElement(element) {

  const id = element.id;
  element.setAttribute("class",
      "tasklist_node_chosen shadowed_chosen_element")
  toggledElement = element

  const comment = document.getElementById("comment" + id)
  const title = document.getElementById("title" + id)
  const place = document.getElementById("place" + id)
  const time = document.getElementById("time" + id)
  const removeButton = document.getElementById("remove_btn" + id);

  removeButton.removeAttribute("hidden");

  comment.setAttribute("class", "task_commentData_chosen");
  comment.removeAttribute("readonly");

  title.setAttribute("class", "task_titleData_chosen");
  title.removeAttribute("readonly");

  place.setAttribute("class", "task_placeData_chosen");
  place.removeAttribute("readonly");

  time.setAttribute("class", "task_timeData_chosen");
  time.removeAttribute("readonly");
}

/** Changes the state of "toggledElement" to untoggled */
async function untoggleElement() {
  if (toggledElement === null) {
    return;
  }

  const id = toggledElement.id;
  toggledElement.setAttribute("class",
      "tasklist_node shadowed_element");

  const comment = document.getElementById("comment" + id)
  const title = document.getElementById("title" + id)
  const place = document.getElementById("place" + id)
  const time = document.getElementById("time" + id)

  const removeButton = document.getElementById("remove_btn" + id);
  removeButton.setAttribute("hidden", "hidden");

  comment.setAttribute("class", "task_commentData");
  comment.setAttribute("readonly", "readonly");

  title.setAttribute("class", "task_titleData");
  title.setAttribute("readonly", "readonly");

  place.setAttribute("class", "task_placeData");
  place.setAttribute("readonly", "readonly");

  time.setAttribute("class", "task_timeData");
  time.setAttribute("readonly", "readonly");

  const changeRequestText = "type=change&number=" + id
      + "&title=" + title.value
      + "&comment=" + comment.value
      + "&place=" + place.value
      + "&time=" + time.value;

  const req1 = fetchHelper("/update-server-task-list", changeRequestText);
  const req2 = fetchHelper("/update-local-task-list", changeRequestText);

  await req1;
  await req2;
  toggledElement = null;
}

/** Process click on the element of the list.
 If no element chosen yet, current becomes more dark to show user that this one is chosen.
 If something is already chosen, it becomes unchosen, all information from this sends to a server.
 Clicking means that the user want to edit this task or remove it. */
function doToggleEvent(event) {
  console.log(event)

  if (event.target.localName === "img") {
    console.log("click on remove");
    return;
  }

  let currentElement;
  for (const view of event.path) {
    if (view.localName === "li") {
      currentElement = view;
    }
  }

  /** Clicks on input shouldn't untoggle the task */
  if (toggledElement === currentElement && event.target.localName === "input") {
    console.log("input click");
    return;
  }

  /** If any task is already chosen, it should be untoggled */
  if (toggledElement != null) {
    /** If user click the task which is already chosen, it becomes not chosen */
    if (currentElement === toggledElement) {
      untoggleElement()
      return
    } else {

      untoggleElement();
    }
  }

  toggleElement(currentElement)
}

function createListElement(task) {
  const liElement = document.createElement("li");
  liElement.setAttribute("class", "tasklist_node shadowed_element");
  liElement.setAttribute("id", task.datastoreId);

  liElement.addEventListener("click", doToggleEvent)

  liElement.appendChild(buildMainTaskDataPanel(task));
  liElement.appendChild(buildTaskRightPanel(task))
  return liElement;
}

async function addNewView(event) {
  untoggleElement();

  console.log(event);
  const requestParams = "type=add&task-text=title&task-place=place&task-comment=comment&task-date=date&task-time=time";
  const response = await fetchHelper("/update-local-task-list", requestParams);

  const task = await response.json();

  const newListElement = createListElement(task);
  document.getElementById('task-container')
  .appendChild(newListElement);
  toggleElement(newListElement);
}

async function buildComposeButton() {
  const btnElement = document.getElementById("task-composer-button");
  btnElement.addEventListener("click", addNewView);
}

async function doPreparation() {
  await loadToDos();
  await buildComposeButton();
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
  var mapMarkers = {};
  var mapInfos = {};
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
  const response = await fetch('/update-local-task-list');
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
          handleLocationError('The Geolocation service failed.', infoWindow, map.getCenter());
        }
    );
  } else {
    // Browser doesn't support Geolocation
    handleLocationError('Your browser does not support geolocation', infoWindow, map.getCenter());
  }
}

function handleLocationError(browserGeoState, infoWindow, pos) {
  infoWindow.setPosition(pos);
  infoWindow.setContent(`Error: ${browserGeoState}`);
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

