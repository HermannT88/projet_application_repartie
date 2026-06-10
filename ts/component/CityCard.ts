import { VILLES } from "../config.mjs";

const VILLES_PAYS: Record<VILLES, string> = {
    [VILLES.AMIENS]:     'France',
    [VILLES.BESANCON]:   'France',
    [VILLES.LYON]:       'France',
    [VILLES.MULHOUSE]:   'France',
    [VILLES.NANCY]:      'France',
    [VILLES.NANTES]:     'France',
    [VILLES.TOULOUSE]:   'France',
    [VILLES.BRUXELLES]:  'Belgique',
    [VILLES.NAMUR]:      'Belgique',
    [VILLES.DUBLIN]:     'Irlande',
    [VILLES.LJUBLJANA]:  'Slovénie',
    [VILLES.LUXEMBOURG]: 'Luxembourg',
    [VILLES.SEVILLE]:    'Espagne',
    [VILLES.VALENCE]:    'Espagne',
    [VILLES.TOYAMA]:     'Japon',
};

class CityCards extends HTMLElement {
    connectedCallback(): void {
        this.innerHTML = Object.values(VILLES)
            .map(ville => {
                const nom = ville.charAt(0).toUpperCase() + ville.slice(1);
                const pays = VILLES_PAYS[ville];
                return `<div class="city-card" id="${ville}">
                    <b>${nom}</b>
                    <i>${pays}</i>
                </div>`;
            })
            .join('');
    }
}

customElements.define('city-cards', CityCards);