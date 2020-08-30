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
    if (view.localName === "li") {
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
