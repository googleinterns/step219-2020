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
    time: {
      date: "date"
    },
    comment: "comment",
    title: "debug"
  }))

  for (const task of tasksList) {
    container.appendChild(createListElement(task));
  }
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
