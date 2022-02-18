<%@ include file="components/jhead.jsp" %>

<!DOCTYPE html>
<html>
	<head>
		<%@ include file="components/head.jsp" %>
		
		<style>
			@charset "UTF-8";

			@import url('https://fonts.googleapis.com/css2?family=Share+Tech+Mono&display=swap');
			
			* {
				margin-block: 0;
			}
			
			body, html {
				box-sizing: border-box;
				padding: 0;
				margin: 0;
				min-height: 100vh;
				background-color: #7d89c5;; 
			}
			
			ul {
				display: flex;
				justify-content: center;
				gap: 20px;
			}
			
			ul li {
				padding: 3px 10px;
				list-style: none;
				text-decoration: none;
				color: #CE4088;
			}
			
			#fis_table{
				font-family: 'Share Tech Mono', monospace;
				width: 100%;
				background-color: #3e4a89;
				text-align: center;
				color: #FFFFFF;
				font-size: 48px;
				table-layout: fixed;
				border-collapse: collapse;
			}
			
			#fis_table thead th {
				padding: 10px 0px;
				font-weight: 900;
			}
			
			#fis_table tbody td {
				border-top: 1px solid #FFFFFF;
				padding: 30px 0px
			}
			
			#fis_table tbody tr:nth-child(odd) td {
				background-color: #7d89c5;
			}
			
			.fr {
				display: none;
			}
			
		</style>
		
		<script>
			let lang = 0;
			const flight = [
				{
					destination: "London",
					destination_fr: "Londres",
					airline_code: "AF",
					flight_number: "1724",
					gate_number: "4A",
					departure_time: "11:02",
					status: "On Time",
					status_fr: "A l'heure"
				},
				{
					destination: "Amsterdam",
					destination_fr: "Amsterdam",
					airline_code: "KL",
					flight_number: "1735",
					gate_number: "3E",
					departure_time: "11:09",
					status: "On Time",
					status_fr: "A l'heure"
				},
				{
					destination: "Madeira",
					destination_fr: "Madère",
					airline_code: "TO",
					flight_number: "5524",
					gate_number: "6I",
					departure_time: "11:22",
					status: "On Time",
					status_fr: "A l'heure"
				},
				{
					destination: "Ajaccio",
					destination_fr: "Ajaccio",
					airline_code: "AF",
					flight_number: "6743",
					gate_number: "4E",
					departure_time: "11:22",
					status: "On Time",
					status_fr: "A l'heure"
				},
				{
					destination: "Reykjavik",
					destination_fr: "Reykjavik",
					airline_code: "AF",
					flight_number: "667",
					gate_number: "8B",
					departure_time: "11:40",
					status: "On Time",
					status_fr: "A l'heure"
				}
			];
			
			document.addEventListener('DOMContentLoaded', () => {
				refreshDisplay();
				setInterval(refreshDisplay, 3000);
				
			});
			
			function refreshDisplay() {
				lang = !lang;
				document.querySelectorAll(".en").forEach(e => e.style.display = lang ? "inline" : "none");
				document.querySelectorAll(".fr").forEach(e => e.style.display = !lang ? "inline" : "none");
				let dest = lang ? "destination" : "destination_fr";
				let status = lang ? "status" : "status_fr";
				let sBody = "";
				
				for (let i = 0; i < flight.length; i++){
					sBody +=	"<tr>";
					sBody +=		"<td>" + flight[i][dest] + "</td>";
					sBody +=		"<td>" + flight[i].airline_code + "</td>";
					sBody +=		"<td>" + flight[i].flight_number + "</td>";
					sBody +=		"<td>" + flight[i].gate_number + "</td>";
					sBody +=		"<td>" + flight[i].departure_time + "</td>";
					sBody +=		"<td>" + flight[i][status] + "</td>";
					sBody +=	"</tr>";
				}
				
				document.getElementById("table_body").innerHTML = sBody;
			}
			
		</script>
		
	</head>
	
	<body>
		<table id="fis_table">
			<thead>
				<tr>
					<th colspan="6"><span class="en">DEPARTURE</span><span class="fr">DEPART</span></th>
				</tr>
				<tr>
					<th><span class="en">Destination</span><span class="fr">Destination</span></th>
					<th><span class="en">Airline</span><span class="fr">Compagnie</span></th>
					<th><span class="en">Flight</span><span class="fr">Vol</span></th>
					<th><span class="en">Gate</span><span class="fr">Porte</span></th>
					<th><span class="en">Time</span><span class="fr">Heure</span></th>
					<th><span class="en">Status</span><span class="fr">Etat</span></th>
				</tr>				
			</thead>
			<tbody id="table_body">
				<tr>
					<td>Montpellier</td>
					<td>AF</td>
					<td>1578</td>
					<td>F24</td>
					<td>12:34</td>
					<td>Embarquement</td>
				</tr>
				<tr>
					<td>Bordeaux</td>
					<td>AF</td>
					<td>7785</td>
					<td>F28</td>
					<td>12:42</td>
					<td>A l'heure</td>
				</tr>
				<tr>
					<td>Boston</td>
					<td>AA</td>
					<td>712</td>
					<td>A52</td>
					<td>12:58</td>
					<td>Retardé 13:30</td>
				</tr>
			</tbody>
		</table>
	</body>
</html>