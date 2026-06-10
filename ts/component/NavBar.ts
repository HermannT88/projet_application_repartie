class NavBar extends HTMLElement {
    connectedCallback(): void {
        const isHtmlFolder = window.location.pathname.includes('/html/');
        const prefix = isHtmlFolder ? '../' : './';

        this.innerHTML = `
        <nav class="main-nav">
            <ul>
                <li><a href="${prefix}index.html">Accueil</a></li>
                <li><a href="${prefix}html/velib.html">Carte des Vélib</a></li> 
                <li><a href="${prefix}html/carte-restaurant.html">Carte des restaurants</a></li>
                <li><a href="${prefix}html/carte-incidents.html">Carte des incidents</a></li>
                <li><a href="${prefix}html/archi.html">Architecture du projet</a></li>
            </ul>
        </nav>
        `;
    }
}

customElements.define('nav-bar', NavBar);