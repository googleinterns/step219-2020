/** Element which is chosen by user for now */
let toggledElement = null;
let autocompleteForm = null;

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
      calendarDate: "Jan 1, 2020 12:00:00 AM",
    },
    comment: "comment",
    title: "debug",
    isDone: true
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

function createTaskCommentElement(task) {
  const taskCommentElement = document.createElement("div");
  taskCommentElement.setAttribute("class", "task_commentData");
  taskCommentElement.setAttribute("id", "comment" + task.datastoreId);
  taskCommentElement.setAttribute("value", task.comment);
  taskCommentElement.innerText = task.comment;
  taskCommentElement.setAttribute("contentEditable", "false")

  return taskCommentElement;
}

function createTaskTimeElement(task) {
  const taskTimeElement = document.createElement("input");
  taskTimeElement.setAttribute("class", "task_timeData");
  taskTimeElement.setAttribute("type", "time");
  taskTimeElement.setAttribute("id", "time" + task.datastoreId);
  taskTimeElement.setAttribute("readonly", "readonly");
  taskTimeElement.setAttribute("value",
      task.dateTime.calendarDate.split(" ")[1]);
  return taskTimeElement;
}

function createTaskDateElement(task) {
  const taskTimeElement = document.createElement("input");
  taskTimeElement.setAttribute("class", "task_dateData");
  taskTimeElement.setAttribute("type", "date");
  taskTimeElement.setAttribute("max", "2031-01-01")
  taskTimeElement.setAttribute("min", "2000-01-01")
  taskTimeElement.setAttribute("id", "date" + task.datastoreId);
  taskTimeElement.setAttribute("readonly", "readonly");
  taskTimeElement.setAttribute("value",
      task.dateTime.calendarDate.split(" ")[0]);
  return taskTimeElement;
}

function createTaskTitleElement(task) {
  const taskTitleElement = document.createElement("input");
  taskTitleElement.setAttribute("class", "task_titleData");
  taskTitleElement.setAttribute("id", "title" + task.datastoreId);
  taskTitleElement.setAttribute("readonly", "readonly");
  taskTitleElement.setAttribute("value", task.title);

  return taskTitleElement;
}

function createTaskPlaceElement(task) {
  const taskPlaceElement = document.createElement("input");
  taskPlaceElement.setAttribute("class", "task_placeData");
  taskPlaceElement.setAttribute("id", "place" + task.datastoreId);
  taskPlaceElement.setAttribute("readonly", "readonly");
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
    await untoggleElement();
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

async function doSomethingWithAPI() {
  const url = new URL(
      "https://maps.googleapis.com/maps/api/place/queryautocomplete/json?language=en&key=key&input=input");
  url.searchParams.set("key", config.API_KEY);
  url.searchParams.set("input", "Chelyabinsk, Molodo");
  console.log(url.toString());
  return fetch(url.toString(), {
    method: 'GET',
    mode: "no-cors"
  })
}

async function doDoneEvent(event) {
  console.log(event)
  const listView = findParentListView(event);

  const doneAlready = event.target.classList.contains("marked_done_button")

  if (!doneAlready) {
    event.target.classList.add("marked_done_button");
    listView.className = "tasklist_node_done_chosen";

    await untoggleElement();
  } else {
    event.target.classList.remove("marked_done_button");
    listView.setAttribute("class", "tasklist_node_chosen shadowed_element");
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

  const toggleButton = document.createElement("img")
  toggleButton.classList.add("done_button");

  if (task.isDone) {
    toggleButton.classList.add("marked_done_button");
  }

  toggleButton.addEventListener("click", doDoneEvent);
  toggleButton.setAttribute("src", "./images/done-24px.svg");
  toggleButton.setAttribute("id", "done_btn" + task.datastoreId);
  toggleButton.setAttribute("hidden", "hidden");

  buttonHolder.appendChild(toggleButton);
  buttonHolder.appendChild(removeButton);
  return buttonHolder;
}

function buildTaskRightPanel(task) {
  const taskRightPanel = document.createElement("div");

  taskRightPanel.setAttribute("class", "task_rightPanel");

  taskRightPanel.appendChild(createButtonElements(task));
  taskRightPanel.appendChild(createTaskTimeElement(task));
  taskRightPanel.appendChild(createTaskDateElement(task));
  taskRightPanel.appendChild(createTaskPlaceElement(task));
  return taskRightPanel;
}

function buildMainTaskDataPanel(task) {
  const mainTaskPanel = document.createElement("div");
  mainTaskPanel.classList.add("task_mainPanel");

  mainTaskPanel.appendChild(createTaskTitleElement(task));
  mainTaskPanel.appendChild(createTaskCommentElement(task));
  return mainTaskPanel;
}

/** Changes the state of given element to toggled */
function toggleElement(element) {

  const id = element.id;

  element.classList.replace("tasklist_node_default", "tasklist_node_chosen");
  element.classList.replace("tasklist_node_done", "tasklist_node_done_chosen");

  toggledElement = element

  const comment = document.getElementById("comment" + id)
  const title = document.getElementById("title" + id)
  const place = document.getElementById("place" + id)
  const time = document.getElementById("time" + id)
  const date = document.getElementById("date" + id);
  const removeButton = document.getElementById("remove_btn" + id);
  const doneButton = document.getElementById("done_btn" + id);

  autocompleteForm = new google.maps.places.Autocomplete(place)

  removeButton.removeAttribute("hidden");
  doneButton.removeAttribute("hidden");

  comment.setAttribute("class", "task_commentData_chosen");
  comment.contentEditable = "true";

  title.setAttribute("class", "task_titleData_chosen");
  title.removeAttribute("readonly");

  place.setAttribute("class", "task_placeData_chosen");
  place.removeAttribute("readonly");

  time.setAttribute("class", "task_timeData_chosen");
  time.removeAttribute("readonly");

  date.setAttribute("class", "task_dateData_chosen");
  date.removeAttribute("readonly");
}

/** Changes the state of "toggledElement" to untoggled */
async function untoggleElement() {
  if (toggledElement === null) {
    return;
  }
  const id = toggledElement.id;
  toggledElement.classList.replace("tasklist_node_chosen",
      "tasklist_node_default");
  toggledElement.classList.replace("tasklist_node_done_chosen",
      "tasklist_node_done");

  const comment = document.getElementById("comment" + id)
  const title = document.getElementById("title" + id)
  const place = document.getElementById("place" + id)
  const time = document.getElementById("time" + id)
  const date = document.getElementById("date" + id);

  const removeButton = document.getElementById("remove_btn" + id);
  const doneButton = document.getElementById("done_btn" + id);

  removeButton.setAttribute("hidden", "hidden");
  doneButton.setAttribute("hidden", "hidden");

  comment.setAttribute("class", "task_commentData");
  comment.contentEditable = "false";

  title.setAttribute("class", "task_titleData");
  title.setAttribute("readonly", "readonly");

  place.setAttribute("class", "task_placeData");
  place.setAttribute("readonly", "readonly");

  time.setAttribute("class", "task_timeData");
  time.setAttribute("readonly", "readonly");

  date.setAttribute("class", "task_dateData");
  date.setAttribute("readonly", "readonly");

  let lat = 0;
  let lng = 0;
  let areChanged = false;
  if (autocompleteForm.getPlace() !== undefined) {
    lat = autocompleteForm.getPlace().geometry.location.lat()
    lng = autocompleteForm.getPlace().geomerty.location.lng()
    areChanged = true
  }

  const changeRequestParams2 = new URLSearchParams({
    'type': 'change',
    'number': id,
    'title': title.value,
    'comment': comment.innerText,
    'place': place.value,
    'time': time.value,
    'date': date.value,
    'isDone': doneButton.classList.contains("marked_done_button"),
    'lat': lat,
    'lng': lng,
    'coordinates_changed': areChanged
  });

  autocompleteForm = null;

  const changeRequestText2 = changeRequestParams2.toString();
  console.log(changeRequestText2)

  const req1 = fetchHelper("/update-server-task-list", changeRequestText2);
  const req2 = fetchHelper("/update-local-task-list", changeRequestText2);

  await req1;
  await req2;
  toggledElement = null;
}

/** Process click on the element of the list.
 If no element chosen yet, current becomes more dark to show user that this one is chosen.
 If something is already chosen, it becomes unchosen, all information from this sends to a server.
 Clicking means that the user want to edit this task or remove it. */
async function doToggleEvent(event) {
  console.log("Toggling event")
  console.log(event)

  /** Clicks on buttons in right corner shouldn't untoggle the task */
  if (event.target.localName === "img") {
    console.log("click on remove or done");
    return;
  }

  let currentElement;
  for (const view of event.path) {
    if (view.localName === "li") {
      currentElement = view;
    }
  }

  /** Clicks on input shouldn't untoggle the task */
  if (toggledElement === currentElement
      && (event.target.isContentEditable || event.target.localName
          === "input")) {
    console.log("input click");
    return;
  }

  /** If any task is already chosen, it should be untoggled */
  if (toggledElement != null) {
    /** If user click the task which is already chosen, it becomes not chosen */
    if (currentElement === toggledElement) {
      await untoggleElement()
      return
    } else {

      await untoggleElement();
    }
  }

  await toggleElement(currentElement)
}

function createListElement(task) {
  const liElement = document.createElement("li");

  if (task.isDone) {
    liElement.setAttribute("class", "tasklist_node_done");
  } else {
    liElement.setAttribute("class", "tasklist_node_default shadowed_element");
  }

  liElement.setAttribute("id", task.datastoreId);

  liElement.addEventListener("click", doToggleEvent)

  liElement.appendChild(buildMainTaskDataPanel(task));
  liElement.appendChild(buildTaskRightPanel(task))
  return liElement;
}

async function addNewView(event) {
  await untoggleElement();

  console.log(event);
  const requestParams = "type=add&task-text=title&task-place=place&task-comment=comment&task-date=2020-01-01&task-time=00:00";
  const response = await fetchHelper("/update-local-task-list", requestParams);

  const task = await response.json();

  const newListElement = createListElement(task);
  document.getElementById('task-container')
  .appendChild(newListElement);
  await toggleElement(newListElement);
}

async function buildComposeButton() {
  const btnElement = document.getElementById("task-composer-button");
  btnElement.addEventListener("click", addNewView);
}

async function addScript() {
  const script = document.createElement('script');
  script.src = "https://maps.googleapis.com/maps/api/js?key=" + config.API_KEY
  script.async = false;
  script.defer = true;
  document.head.appendChild(script);
}

async function doPreparation() {
  await loadToDos();
  await buildComposeButton();
  // await addScript();
}

async function initMap() {

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

function directionsBetweenMarkers(mapMarkers, directionsService,
    directionsRenderer) {
  for (var marker1 in mapMarkers) {
    for (var marker2 in mapMarkers) {
      if (marker1 != marker2) {
        calculateAndDisplayRoute(directionsService, directionsRenderer,
            mapMarkers[marker1].position, mapMarkers[marker2].position);
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
      mapInfos[markerName].setContent(
          mapInfos[markerName].getContent() + composeNewInfoContent(
          task.number));
    } else {
      mapInfos[markerName] = new google.maps.InfoWindow({
        content: ''
      });
      mapInfos[markerName].setContent(composeNewInfoContent(task.number));
      mapMarkers[markerName] = new google.maps.Marker({
        position: task.place,
        map: map,
        title: markerName,//task.place.string
        icon: {url: "https://maps.google.com/mapfiles/ms/icons/blue-dot.png"}
      });
      mapMarkers[markerName].setIcon(composeIconUrl(task.time));
    }
  }
  for (var markerName in mapMarkers) {
    //explanation of that code is here https://leewc.com/articles/google-maps-infowindow/
    //there was a problem with handling several info windows
    mapMarkers[markerName].infowindow = mapInfos[markerName];
    mapMarkers[markerName].addListener('click', function () {
      return this.infowindow.open(map, this);
    });
    google.maps.event.addListener(mapMarkers[markerName], 'click', function () {
      this.infowindow.open(map, this);
    });
  }
  console.log(mapMarkers);
  console.log(mapInfos);
}

function composeIconUrl(task_time) {
  //change icon color depend on  time
  let urls = "https://maps.google.com/mapfiles/ms/icons/";
  var color = "";
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

function calculateAndDisplayRoute(directionsService, directionsRenderer,
    from_pos, to_pos) {
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
          handleLocationError('The Geolocation service failed.', infoWindow,
              map.getCenter());
        }
    );
  } else {
    // Browser doesn't support Geolocation
    handleLocationError('Your browser does not support geolocation', infoWindow,
        map.getCenter());
  }
}

function handleLocationError(browserGeoState, infoWindow, pos) {
  infoWindow.setPosition(pos);
  infoWindow.setContent(`Error: ${browserGeoState}`);
  infoWindow.open(map);
}

function showHideMap() {
  const mapCurState = document.getElementById('map');
  const mapPanel = document.getElementById('floating-panel');
  const taskContainer = document.getElementById('task-list-view');
  if (mapCurState.style.display === "none") {
    initMap();
    mapCurState.style.display = "block";
    mapPanel.style.display = "block";
    taskContainer.style.display = "none";
  } else {
    mapCurState.style.display = "none";
    mapPanel.style.display = "none";
    taskContainer.style.display = "block";
  }
}

