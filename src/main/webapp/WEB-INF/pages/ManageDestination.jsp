<%@ include file="components/jhead.jsp" %>

<!DOCTYPE html>
<html>
	<head>
		<%@ include file="components/head.jsp" %>
		<link rel="stylesheet" href="${ pageContext.request.contextPath }/resources/css/manager.css">
		<script src="${ pageContext.request.contextPath }/resources/js/manageDestination.js"></script>
		<style>
			
			code {
				background-color: #ffffff45;
			    padding: 2px 5px;
			    border-radius: 5px;
    		}
			
			section.managerFrame {
			    margin: 20px 50px;
			    display: flex;
			    justify-content: end;
			    align-items: flex-start;
			    gap: 20px;
			}
			
			section.managerFrame form {
			    display: flex;
			    flex-direction: column;
			    justify-content: stretch;
			    padding: 0 20px;
			    border: 3px solid #00a6ed;
			    background-color: #e5f7ff;
			    border-radius: 3px;
			}
			
			section.managerFrame form > h4 {
			    border-bottom: 0.2em solid #00a6ed;
			    margin: 20px 0 0;
			}
			
			section.managerFrame form label {
			    display: block;
			    margin-top: 20px;
			}
			
			section.managerFrame form .form_btn {
			    display: flex;
			    justify-content: end;
			    gap: 20px;
			    margin: 20px 0;
			}
			
			section.managerFrame form .form_btn > button {
				font-weight: 600;
			    cursor: pointer;
			    padding: 5px 10px;
			    border-radius: 10px;
			    border: none;
			    box-shadow: 0 0 2px black, 0 0 10px #45454545 inset;
			}
			
			section.managerFrame form .form_btn > button.form_cancel_btn:hover {
				color: #ca1d3a;
			    box-shadow: 0 0 2px black, 0 0 3px #45454545 inset;
			}
			
			section.managerFrame form .form_btn > button.form_submit_btn:hover {
			    color: #27b050;
			    box-shadow: 0 0 2px black, 0 0 3px #45454545 inset;
			}
			
			section.managerFrame form .form_btn > button:active {
				box-shadow: 0 0 1px black, 0 0 3px #45454545 inset !important;
			}
			
			section.managerFrame form .form_btn > button:disabled:hover,
			section.managerFrame form .form_btn > button:disabled:active {
				box-shadow: 0 0 2px black, 0 0 10px #45454545 inset!important;
				cursor: not-allowed;
			}
			
			section.managerFrame table {
				margin: 0;
			    flex-grow: 1;
			    border-collapse: collapse;
			    text-align: center;
			    border: 3px solid #00a6ed;
			}
			
			section.managerFrame table td, table th {
			    border: 1px solid #00a6ed;
			}
			
			section.managerFrame table th {
			    padding: 10px 0;
			    background-color: #e5f7ff;
			}
			
			section.managerFrame .table_action {
			    width: 100px;
			}
			
			section.managerFrame table .table_action > span {
			    margin: 5px;
			    padding: 3px;
			    border-radius: 50%;
			    text-decoration: none;
			    user-select: none;
			    cursor: pointer;
			    color: #454545;
			    box-shadow: 0 0 2px black, 0 0 10px #45454545 inset;
			}
			
			section.managerFrame table .table_action > span.delete:hover {
			    color: #ca1d3a;
			    box-shadow: 0 0 2px black, 0 0 3px #45454545 inset;
			}
			
			section.managerFrame table .table_action > span.edit:hover{
			    color: #e7c83a;
			    box-shadow: 0 0 2px black, 0 0 3px #45454545 inset;
			}
			
			section.managerFrame table .table_action > span:active {
				box-shadow: 0 0 2px #45454545, 0 0 3px #45454545 inset !important;
			}
			
			#table_body_loading span{
				color: #454545;
				font-size: 3em;
				animation-name: spin;
				animation-duration: 4s;
				animation-iteration-count: infinite;
				animation-timing-function: cubic-bezier(0.26, -0.55, 0.47, 1.63);
			}
			
			@keyframes spin {
				from {
					transform: rotate(0deg);
				}
				
				to {
					transform: rotate(1080deg);
				}
			}
			
		</style>
		<style>
			#delete-form-entry-modal {
				display: none;
			    width: 80%;
				position: absolute;
			    top: 20%;
			    left: 10%;
			    overflow: hidden;
			    box-shadow: 0 0 3px #454545, 0 0 10px #45454545 inset;
			    border-radius: 20px;
			    background-color: #ffffff;
			}
			
			#delete-form-entry-modal h4 {
				font-size: 1em;
			    color: white;
			    margin: 0;
			    padding: 15px 20px;
			    text-align: center;
				background-color: #454545;
			}
			
			#delete-form-entry-modal .modal_btn {
				display: flex;
				justify-content: end;
				gap: 20px;
			    background: #f2f2f2;
    			padding: 10px 20px;
			}
			
			.modal_content{
				padding: 0 20px;
			}
			
			body.modalLocked::before {
				position: fixed;
				content: "";
				height: 100vh;
				width: 100vw;
				background-color: #45454590;
			}
		</style>
		
		<style>
			/* Buton Style */
			.btn {
				cursor: pointer;
			    padding: 5px 10px;
			    border-radius: 10px;
			    border: none;
			    box-shadow: 0 0 2px black, 0 0 10px #45454545 inset;
			}
			
			.btn:hover {
			    box-shadow: 0 0 2px black, 0 0 3px #45454545 inset;
			}
			
			.btn:active {
				box-shadow: 0 0 1px black, 0 0 3px #45454545 inset !important;
			}
			
			.btn:disabled:hover,
			.btn:disabled:active {
				box-shadow: 0 0 2px black, 0 0 10px #45454545 inset!important;
				cursor: not-allowed;
			}
			
			.btn_primary {
				background-color: #e5f7ff;
			}
			
		</style>
	</head>
	
	<body>
		<%@ include file="components/nav.jsp" %>

		<section id="manageDestination" class="managerFrame">
			<table>
	            <thead>
	                <tr>
	                    <th>Name (EN)</th>
	                    <th>Name (FR)</th>
	                    <th>Airport Code</th>
	                    <th class="table_action">Actions</th>
	                </tr>
	            </thead>
	            <tbody id="table_body">
	            	<tr id="table_body_loading"><td colspan="4"><span class="material-icons">autorenew</span></td></tr>
	            </tbody>
	        </table>
	        <form id="main_form">
	            <h4 id="main_form_title">Create a New Destination</h4>
	            <div>
	                <label for="destination_name">Destination Name</label>
	                <input type="text" id="destination_name" name="destination_name" placeholder="Beijing (EN)" required>
	                <input type="text" id="destination_name_fr" name="destination_name_fr" placeholder="Pékin (FR)">
	            </div>
	
	            <div>
	                <label for="destination_airport_code">Airport Code</label>
	                <input type="text" id="destination_airport_code" name="destination_airport_code" placeholder="ZBAA" required>
	            </div>
	
	            <div class="form_btn">
	                <button class="form_cancel_btn" type="reset">Cancel</button>
	                <button class="form_submit_btn" type="submit">Submit</button>
	            </div>
	        </form>
		</section>
		
		<div id="delete-form-entry-modal" class="modal">
			<h4>Are you sure you want to delete <code data-var="name">$CityName</code> from Database ?</h4>
			<div class="modal_content">
				<p><span>Loading :</span>Checking <code data-var="name">$CityName</code> database usage...</p>
			</div>
			<div class="modal_btn">
                <button class="btn " type="reset">No</button>
                <button class="btn btn_primary" type="submit" disabled>Yes</button>
            </div>
		</div>
	</body>
</html>