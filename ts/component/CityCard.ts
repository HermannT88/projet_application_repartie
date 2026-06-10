import { VILLES, VILLES_COORDONEES } from "../config.mjs";

class CityCards extends HTMLElement {
    connectedCallback(): void {
        this.innerHTML = Object.values(VILLES)
            .map(ville => `<div class="city-card" id="${ville}">${ville}</div>`)
            .join('');
    }
}

customElements.define('city-cards', CityCards);