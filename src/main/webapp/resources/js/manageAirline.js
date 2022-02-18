/**
 * JS Function for ManageDestination Page
 */
let tableHandler;
 
 
document.addEventListener('DOMContentLoaded', () => {
    console.log(`Document is ready!`);
    tableHandler = new AirlineTable();
	document.getElementById('main_form').addEventListener('submit', e => {tableHandler.submitForm(e)});
	document.getElementById('main_form').addEventListener('reset', tableHandler.clearForm);
	document.querySelector('#delete-form-entry-modal button[type=reset]').addEventListener('click', tableHandler.closeModal);
	document.querySelector('#delete-form-entry-modal button[type=submit]').addEventListener('click', tableHandler.deleteEntry.bind(tableHandler));
    tableHandler.loadData();
});


class AirlineTable {
	#colsCount = 4;
	#responseData = [];
	#responseStatus;
	#eventsReference = {
		isSet: false,
		edit: null,
		delete: null
	};
	
	#buildTableBody() {
		let trList = "";
		
		if (this.#responseStatus === "OK" && this.#responseData.length > 0){
			for (const airline of this.#responseData) {
				trList +=`<tr>`;
				trList +=	`<td>${ airline.airline_code }</td>`;
				trList +=	`<td>${ airline.airline_name }</td>`;
				trList +=	`<td>${ airline.airline_thumbnail_url }</td>`;
				trList +=	`<td class="table_action">`;
				trList +=		`<span class="material-icons edit" data-id="${ airline.airline_id }">edit</span>`;
				trList +=		`<span class="material-icons delete" data-id="${ airline.airline_id }">delete_forever</span>`;
				trList +=	`</td>`;
				trList +=`</tr>`;
			}
		}
		else if (!this.#responseStatus === "OK"){
			trList = `<tr><td colspan="${ this.#colsCount }">Fetch Data Failed !</td></tr>`;
		}
		else {
			trList = `<tr><td colspan="${ this.#colsCount }">No data</td></tr>`;
		}
		
		document.getElementById("table_body").innerHTML = trList;
		
		this.#generateFormEventListeners();
	}
	
	/** Database connectors **/
	
	/**
	 *	READ
	 */
	loadData() {
		
		document.getElementById("table_body").innerHTML = `<tr id="table_body_loading"><td colspan="${ this.#colsCount }"><span class="material-icons">autorenew</span></td></tr>`;
		
		const reqBody = {};
		
		fetch("api/airline/get", {
			method: 'post',
			headers: {
				'Content-Type': 'application/json;charset=utf-8'
			},
			body: JSON.stringify(reqBody)
		})
		.then(res => res.json())
		.then(data => {
			this.#responseData = data.data;
			this.#responseStatus = data.status;
			this.#buildTableBody();
		});
	}
	
	submitForm(e){
		e.preventDefault();
		this.#disableForm();
		
		const airline_code = document.getElementById('airline_code').value.toUpperCase();
		const airline_name = document.getElementById('airline_name').value;
		const airline_id = document.getElementById('airline_name').getAttribute("data-updating-id");
		
		if(this.#validCodeValue(airline_code) && this.#validNameValue(airline_name)){
			const reqBody = {
				"airline_id": airline_id,
				"airline_code": airline_code,
				"airline_name": airline_name
			};
			
			console.log(reqBody)

			fetch("api/airline/set", {
				method: 'post',
				headers: {
					'Content-Type': 'application/json;charset=utf-8'
				},
				body: JSON.stringify(reqBody)
			})
			.then(res => res.json())
			.then(data => {
				if (data.status == "OK"){
					this.clearForm();
					if(airline_id === null) {
						this.#responseData.unshift(data.data);
					}
					else {
						const uIndex = this.#responseData.findIndex(element => element.airline_id == reqBody.airline_id);
						this.#responseData[uIndex].airline_code = airline_code;
						this.#responseData[uIndex].airline_name = airline_name;
					}
					this.#buildTableBody();
				}
				else {
					console.log(`Error executing ${ reqBody.action } request`);
				}
			});
		}
		else {
			//TODO Not valid inputs
			this.clearForm();
		}
		
	}
	
	closeModal() {
		document.querySelector('body').classList.remove('modalLocked');
		document.getElementById('delete-form-entry-modal').style.display = 'none';
	}

	#confirmDelete(e){
		const id = e.target.getAttribute('data-id');
		const deletingDest = this.#getDataForDataId(id);

		document.querySelector('#delete-form-entry-modal button[type=submit]').setAttribute('data-id', id);
		document.querySelector('#delete-form-entry-modal code[data-var=name]').innerHTML = deletingDest.destination_name;
		document.querySelector('body').classList.add('modalLocked');
		document.getElementById('delete-form-entry-modal').style.display = 'block';

		const reqBody = {
			"action": "isSafeToDelete",
			"destination_id": id,
		};
		
		fetch("api/destination", {
			method: 'post',
			headers: {
				'Content-Type': 'application/json;charset=utf-8'
			},
			body: JSON.stringify(reqBody)
		})
		.then(res => res.json())
		.then(data => {
			if (data.status == "OK"){
				const modalContent = document.querySelector('#delete-form-entry-modal .modal_content');
				let linkedCount = 0;
				let maxRow = 0;
				data.data.forEach(element => {
					element.forEach(el => {
						linkedCount++;
						maxRow = el.length > maxRow ? el.length : maxRow;
					})
				});
				
				if (linkedCount == 0) {
					modalContent.innerHTML = "<p><span>Delete is Safe !</span> No data for foreign key(s) found.</p>";
				}
				else {
					document.querySelector('#delete-form-entry-modal button[type=submit]').innerHTML = "Yes, delete " + (linkedCount + 1) + " entries from database";
					modalContent.innerHTML = "<p><span>Warning :</span> Deleting <code>" + deletingDest.destination_name + "</code> will also delete " + linkedCount + " flight(s) from database :</p>";
					// TODO Generate a view with all data going to be deleted
				}
				document.querySelector('#delete-form-entry-modal button[type=submit]').removeAttribute("disabled");

			}
			else {
				console.log(`Error executing ${ reqBody.action } request`);
			}
		});


	}

	deleteEntry(e) {
		const id = e.target.getAttribute('data-id');

		const reqBody = {
			"action": "delete",
			"destination_id": id,
		};
		
		fetch("api/destination", {
			method: 'post',
			headers: {
				'Content-Type': 'application/json;charset=utf-8'
			},
			body: JSON.stringify(reqBody)
		})
		.then(res => res.json())
		.then(data => {
			if (data.status == "OK"){
				const uIndex = this.#responseData.findIndex(element => element.destination_id == reqBody.destination_id);
				this.#responseData.splice(uIndex, 1);
				this.#buildTableBody();
				this.closeModal();
			}
			else {
				console.log(`Error executing ${ reqBody.action } request`);
			}
		});

	}

	/**
	 *	FORM
	 */
	 
	#loadInForm(e){
		const id = e.target.getAttribute('data-id');
		const updatingDest = this.#getDataForDataId(id);
		document.getElementById('main_form').style.backgroundColor = "#fffce5";
		document.getElementById('main_form').style.borderColor = "#e6cf00";
		document.getElementById('main_form_title').innerHTML = `Update Airline (${ updatingDest.airline_id })`;
		document.getElementById('main_form_title').style.borderColor = "#e6cf00";
		document.getElementById('airline_code').value = updatingDest.airline_code;
		document.getElementById('airline_name').value = updatingDest.airline_name;
		document.getElementById('airline_name').setAttribute("data-updating-id", updatingDest.airline_id);
	}
	 
	#disableForm() {
		const form = document.getElementById('main_form');
		for (const submitBtn of form.getElementsByClassName('form_submit_btn')){
			submitBtn.setAttribute('disabled', 'true');
			submitBtn.innerHTML = '<span class="material-icons">autorenew</span>';
		}
		for (const input of form.getElementsByTagName('input')){
			input.setAttribute('disabled', 'true');
		}
	}
	
	clearForm() {
		const form = document.getElementById('main_form');
		for (const submitBtn of form.getElementsByClassName('form_submit_btn')){
			submitBtn.removeAttribute('disabled');
			submitBtn.innerHTML = 'Submit';
		}
		for (const input of form.getElementsByTagName('input')){
			input.removeAttribute('disabled');
		}

		document.getElementById('main_form').style.backgroundColor = "";
		document.getElementById('main_form').style.borderColor = "";
		document.getElementById('main_form_title').innerHTML = `Create a New Airline`;
		document.getElementById('main_form_title').style.borderColor = "";
		document.getElementById('airline_code').value = "";
		document.getElementById('airline_name').value = "";
		document.getElementById('airline_name').removeAttribute("data-updating-id");
	}
	
	/**
	 *	FORM validator
	 */
	#validNameValue(value){
		const regExp = new RegExp(/^[\s\'\-A-Za-zŒšœžŸÀ-ÖÙ-öø-ÿ]{1,80}$/);
		const validation = regExp.test(value);
		return validation;
	}
	
	#validCodeValue(code){
		const regExp = new RegExp(/^[A-Z]{2}$/);
		const validation = regExp.test(code);
		return validation;
	}

	/**
	 * Get data for id store in the dataset.
	 * Check if data is existing and unique in order to ensure data integrity.
	 * If not, the method handle a page refresh in order to reload all data
	 * TODO We could handle in an other way the data integrity fix than by a refresh.
	 * TODO By handling it, we can make a js check data intergrity in order to report a data issue if not. 
	 * @param {Number} dataId 
	 * @returns dataObject if data is existing and unique, false otherwise (Handle a refresh to try a data fix). 
	 */
	#getDataForDataId(dataId){
		const updatingDest = this.#responseData.filter(element => element.airline_id == dataId);

		if (updatingDest.length === 1) {
			return updatingDest[0];
		}
		else if (updatingDest.length === 0) {
			//No data
			alert("An error has been encountered:\nNo id found in Airline dataset\nPage will be refreshed");
			location.reload();
			return false;
		}
		else {
			//Too many dataId
			alert("An error has been encountered:\nMultiple id found in Airline dataset\nPage will be refreshed");
			location.reload();
			return false;
		}
	}
	
	#generateFormEventListeners() {
		const edits = document.querySelectorAll('.table_action span.edit');
		const deletes = document.querySelectorAll('.table_action span.delete');

		if (this.#eventsReference.isSet){
			//Remove existing Event Listener
			edits.forEach(element => element.removeEventListener('click', this.#eventsReference.edit));
			deletes.forEach(element => element.removeEventListener('click', this.#eventsReference.delete));
		}

		//Rebuild event Listeners
		edits.forEach(element => element.addEventListener('click', this.#eventsReference.edit = this.#loadInForm.bind(this)));
		deletes.forEach(element => element.addEventListener('click', this.#eventsReference.delete = this.#confirmDelete.bind(this)));

		this.#eventsReference.isSet = true;
	}
}

