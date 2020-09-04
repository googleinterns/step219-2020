/** Loads list of user tasks from server and puts it into view*/
async function loadToDos() {
  let response = await fetch('/update-local-task-list');
  let tasksList = await response.json();

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
  for (view of event.path) {
    if (view.localName === "li") {
      return view;
    }
  }
}

async function editFieldData(event) {
  console.log(event)

  const taskView = findParentListView(event);
  const elementView = event.path[0];
  const askResult = prompt("Do you want to change this field?",
      elementView.innerText)
  if (askResult == null) {
    return;
  }
  elementView.innerText = askResult;
  const requestParams = "field=" + elementView.className + "&type=edit&"
      + "new_data=" + askResult + "&number=" + taskView.id;

  let req1 = fetch('/update-server-task-list', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: requestParams
  });

  let req2 = fetch('/update-local-task-list', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: requestParams
  });

  await req1
  await req2;
}

function createTaskCommentElement(task) {
  const taskCommentElement = document.createElement("div");
  taskCommentElement.setAttribute("class", "task_commentData");
  taskCommentElement.addEventListener("click", editFieldData);
  taskCommentElement.innerText = task.comment;
  return taskCommentElement;
}

function createTaskTimeElement(task) {
  const taskTimeElement = document.createElement("input");
  taskTimeElement.setAttribute("class", "task_timeData");
  taskTimeElement.addEventListener("click", editFieldData);
  taskTimeElement.innerText = task.time.date;
  return taskTimeElement;
}

function createTaskTitleElement(task) {
  const taskTitleElement = document.createElement("h4");
  taskTitleElement.setAttribute("class", "task_titleData");
  taskTitleElement.addEventListener("click", editFieldData);
  taskTitleElement.innerText = task.title;
  return taskTitleElement;
}

function createTaskPlaceElement(task) {
  const taskPlaceElement = document.createElement("div");
  taskPlaceElement.setAttribute("class", "task_placeData");
  taskPlaceElement.addEventListener("click", editFieldData);
  taskPlaceElement.innerText = task.place.name;
  return taskPlaceElement;
}

function getConfirmation() {
  return confirm("Do you really want to remove this task?");
}

/** Removes task which is connected with this view */
async function removeElement(view) {
  const notificationText = "type=delete&number=" + view.id;
  await fetch('/update-server-task-list', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: notificationText
  });

  await fetch('/update-local-task-list', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    body: notificationText
  })

  view.remove();
}

function doRemoveEvent(event) {
  const elementId = event.path[2].id;
  const view = document.getElementById(elementId);

  view.setAttribute("class", "chosen_tasklist_node");
  const result = getConfirmation();
  if (result) {
    removeElement(view);
  }
  view.setAttribute("class", "tasklist_node");
}

function createButtonElements() {
  const buttonHolder = document.createElement("div");
  buttonHolder.setAttribute("class", "task_buttonHolder");

  const removeButton = document.createElement("img")
  removeButton.setAttribute("class", "task_button")
  removeButton.addEventListener("click", doRemoveEvent);
  removeButton.setAttribute("src", "./images/clear-48dp.svg");

  buttonHolder.appendChild(removeButton);
  return buttonHolder;
}

function buildTaskRightPanel(task) {
  const taskRightPanel = document.createElement("div");

  taskRightPanel.setAttribute("class", "task_rightPanel");

  taskRightPanel.appendChild(createButtonElements());
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

function createListElement(task) {
  const liElement = document.createElement("li");
  liElement.setAttribute("class", "tasklist_node shadowed_main_element");
  liElement.setAttribute("id", task.datastoreId);

  liElement.appendChild(buildMainTaskDataPanel(task));
  liElement.appendChild(buildTaskRightPanel(task))
  return liElement;
}

function buildComposeView() {
  const composeElement = document.createElement("li");
  composeElement.setAttribute("class", "tasklist_node shadowed_main_element");
  composeElement.appendChild(createTaskCommentForm())
  composeElement.innerText = "Kek";
  return composeElement;
}

function addComposeView(event) {
  const container = document.getElementById('task-container');
  container.appendChild(buildComposeView())
}

async function buildComposeButton() {
  const btnElement = document.getElementById("task-composer-button");
  btnElement.addEventListener("click", addComposeView);
}

async function doPreparation() {
  await loadToDos();
  await buildComposeButton();
}
