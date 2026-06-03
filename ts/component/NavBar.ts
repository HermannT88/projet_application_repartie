class NavBar extends HTMLElement {
    connectedCallback(): void {
        this.innerHTML = `
        <nav class="main-nav">
            <ul>
                <li><a href="/index.html">Accueil</a></li>
                <li><a href="/html/velib.html">Carte des Vélib</a></li> 
                <li><a href="/html/carte-restaurant.html">Carte des restaurants</a></li>
                <li><a href="/html/carte-incidents.html">Carte des incidents</a></li>
            </ul>
        </nav>
        `;
    }
}

customElements.define('nav-bar', NavBar);