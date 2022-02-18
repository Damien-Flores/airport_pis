<nav>
    <ul>
    	<li class="${ page == 'home' ? 'active' : '' }">
            <a href="${ pageContext.request.contextPath}/home">Home</a>
        </li>
        <li>
            <a><span class="material-icons">expand_more</span> Display Selection</a>
            <div class="dropdown">
                <a href="${ pageContext.request.contextPath}/display/public">Public Area</a>
                <a href="#">Boarding Area</a>
            </div>
        </li>
        <li class="${ page == 'manageFlight' ? 'active' : '' }">
            <a href="#">Manage Flights</a>
        </li>
        <li class="${ page == 'manageAirline' ? 'active' : '' }" >
            <a href="${ pageContext.request.contextPath}/manageairline">Manage Airlines</a>
        </li>
        <li class="${ page == 'manageDestination' ? 'active' : '' }">
            <a href="${ pageContext.request.contextPath}/managedestination">Manage Destinations</a>
        </li>
    </ul>
</nav>