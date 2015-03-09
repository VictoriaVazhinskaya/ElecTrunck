var userNameGlobal = 'Username';
var messagesForDeleting = [];
var messageCounter = 0;


function run(){
	var buttonLogin = document.getElementsByClassName('login')[0];
    var buttonChange = document.getElementsByClassName('change-name')[0];
    var buttonSend = document.getElementsByClassName('send-message')[0];
    var messagesList = document.getElementById('correspondence');
    var deleteButton = document.getElementsByClassName('delete')[0];
    var editButton = document.getElementsByClassName('edit')[0];
 	buttonLogin.addEventListener('click', onLoginButtonClick);
 	buttonChange.addEventListener('click', onChangeButtonClick);
    buttonSend.addEventListener('click', onSendButtonClick);
    messagesList.addEventListener('click', emit);
    deleteButton.addEventListener('click', onDeleteButtonClick);
    editButton.addEventListener('click', onEditButtonClick);
}

	

function onLoginButtonClick(){
	var userName = document.getElementById('name-field');

    if(!userName.value){
		return;
	}
	userNameGlobal = userName.value;
	document.getElementsByClassName("current-user")[0].innerHTML = userName.value;
	
	userName.value = "";
	
} 

function onChangeButtonClick(){
	var userName = document.getElementById('name-field');

    if(!userName.value){
		return;
	}
	userNameGlobal = userName.value;
	document.getElementsByClassName("current-user")[0].innerHTML = userName.value;
	
	userName.value = "";
	
} 

function onSendButtonClick(){
	messageCounter++;
	var userName = document.getElementsByClassName('current-user')[0];
	var message =  document.getElementsByClassName('write-message')[0];
	if(!message.value){
		return;
	}
	var itemDiv = document.createElement('div');
	var sender = document.createElement('li');
	var text = document.createTextNode(userNameGlobal);
	itemDiv.classList.add('item');
	itemDiv.setAttribute('id', 'mess'+ messageCounter);
	sender.appendChild(text);
	sender.setAttribute('id', 'user1');
	var sent = document.createElement('li');
	text = document.createTextNode(message.value);
	sent.appendChild(text);
	sent.setAttribute('id', 'message');
	var editBox = document.createElement('dialog');
	editBox.classList.add('edit-message-box');
	var closeDButton = document.createElement('input');
	closeDButton.classList.add('close-dialog');
	closeDButton.setAttribute('type', 'button');
	closeDButton.setAttribute('value', 'x');
	var textForEditting = document.createTextNode(message.value);
	var editText = document.createElement('textarea');
	editText.classList.add('edit-textarea');
	editText.style.backgroundColor = 'LightSkyBlue';
	editText.appendChild(textForEditting);
	var submitButton = document.createElement('input');
	submitButton.setAttribute('type', 'button');
	submitButton.setAttribute('value', 'send');
	submitButton.classList.add('send-edit-button');
	editBox.appendChild(closeDButton);
	editBox.appendChild(editText);
	editBox.appendChild(submitButton);
	itemDiv.appendChild(sender);
	itemDiv.appendChild(sent);
	itemDiv.appendChild(editBox);
	document.getElementById('correspondence').getElementsByTagName('ul')[0].appendChild(itemDiv);
	message.value = "";
	setCaretPosition(document.getElementsByClassName('write-message')[0].id, 10);
}

function emit(evtObj){
	if(evtObj.type === 'click' && evtObj.target.nodeName == 'LI'){
		var clickedMessage = evtObj.target.parentElement;
		if(!clickedMessage.classList.contains("emit-style")){
		clickedMessage.classList.add("emit-style");
		messagesForDeleting.push(clickedMessage.id);
		document.getElementsByClassName('edit')[0].style.backgroundColor = 'red';
		document.getElementsByClassName('delete')[0].style.backgroundColor = 'red';
	}
	else{
		clickedMessage.classList.remove("emit-style");
		messagesForDeleting.splice(messagesForDeleting.indexOf(clickedMessage.id), 1);
		if(messagesForDeleting.length == 0){
        document.getElementsByClassName('edit')[0].style.backgroundColor = 'black';
		document.getElementsByClassName('delete')[0].style.backgroundColor = 'black';
	}
	}

	}

}


function onDeleteButtonClick(){
	var parentElement = document.getElementById('correspondence').getElementsByTagName('ul')[0];
    for(var i=0; i<messagesForDeleting.length; i++){
    	var childElement = document.getElementById(messagesForDeleting[i]);
    	parentElement.removeChild(childElement);
    }
    messagesForDeleting.splice(0, messagesForDeleting.length);
    if(messagesForDeleting.length == 0){
        document.getElementsByClassName('edit')[0].style.backgroundColor = 'black';
		document.getElementsByClassName('delete')[0].style.backgroundColor = 'black';
	}
}

function onEditButtonClick(){
	var size = messagesForDeleting.length;
	var childElement = document.getElementById(messagesForDeleting[size-1]);
	childElement.getElementsByTagName('dialog')[0].show();
	var closeDialogBoxButton = childElement.getElementsByTagName('dialog')[0].getElementsByClassName('close-dialog')[0];
	var sendEditMessageButton = childElement.getElementsByTagName('dialog')[0].getElementsByClassName('send-edit-button')[0];
    var editTextArea = childElement.getElementsByTagName('dialog')[0].getElementsByTagName('textarea')[0];
    closeDialogBoxButton.addEventListener('click', onCloseDButtonClick);
	sendEditMessageButton.addEventListener('click', onEditInsightDBoxButtonClick);
	editTextArea.addEventListener('click', changeEditTextAreaBGColor);
}

function onCloseDButtonClick(evtObj){
	var dialogBox = evtObj.target.parentElement;
	var currentMessage = dialogBox.parentElement;
	dialogBox.close();
    currentMessage.classList.remove("emit-style");
	messagesForDeleting.splice(messagesForDeleting.indexOf(currentMessage.id), 1);
	if(messagesForDeleting.length == 0){
        document.getElementsByClassName('edit')[0].style.backgroundColor = 'black';
		document.getElementsByClassName('delete')[0].style.backgroundColor = 'black';
	}
}

function changeEditTextAreaBGColor(evtObj){
	var dialogBox = evtObj.target.parentElement;
	dialogBox.getElementsByTagName('textarea')[0].style.backgroundColor = "White";
}

function onEditInsightDBoxButtonClick(evtObj){
	
		var dialogBox = evtObj.target.parentElement;
		var oldMessage =  dialogBox.parentElement;
		var newMessage = dialogBox.getElementsByTagName('textarea')[0];
        if(!newMessage.value){
        	return;
        }

        oldMessage.getElementsByTagName('li')[1].innerHTML = newMessage.value;
        oldMessage.getElementsByTagName('dialog')[0].close();
	    
}

function setCaretPosition(elemId, caretPos) {
    var elem = document.getElementById(elemId);

    if(elem != null) {
        if(elem.createTextRange) {
            var range = elem.createTextRange();
            range.move('character', caretPos);
            range.select();
        }
        else {
            if(elem.selectionStart) {
                elem.focus();
                elem.setSelectionRange(caretPos, caretPos);
            }
            else
                elem.focus();
        }
    }
}