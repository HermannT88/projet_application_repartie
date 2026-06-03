class NavBar extends HTMLElement {
    connectedCallback(): void {
        this.innerHTML = `
        <nav class="main-nav">
            <ul>
                <li><a href="/index.html">Accueil</a></li>
                <li><a href="/html/velib.html">Carte Vélib</a></li> 
                <li><a href="/html/carte-restaurant.html">Carte Restaurant</a></li>
                <li><a href="/html/carte-incidents.html">Carte Incidents</a></li>
            </ul>
        </nav>
        `;
    }
}

customElements.define('nav-bar', NavBar);