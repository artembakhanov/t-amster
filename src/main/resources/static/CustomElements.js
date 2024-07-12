console.log(window.Telegram.WebApp.initDataUnsafe.user);

const tg = window.Telegram.WebApp;
tg.disableVerticalSwipes();
tg.expand();

function checkIfReady(resolve) {
    const interval = setInterval(() => {
        if (Korolev.ready) resolve(interval);
    }, 100);
}

function ready(interval) {
    clearInterval(interval)
    let user = window.Telegram.WebApp.initDataUnsafe.user;

    Korolev.invokeCallback('login', JSON.stringify({ id: user.id, firstName: user.first_name, lastName: user.last_name }));
    this.document.body.dispatchEvent(new Event("login", { bubbles: true }));
    tg.ready();
}

window.addEventListener('load', function () {
    const waitUntilReady = new Promise(checkIfReady);
    waitUntilReady.then(ready);
});

class TouchWrap extends HTMLElement {
    constructor() {
        super();
        this.eventIndex = 0;
        this.events = [
            't0',
            't1',
            't2',
            't3',
            't4',
            't5',
            't6',
            't7',
            't8',
            't9'
        ];
    }

    connectedCallback() {
        if (['tdesktop', 'macos'].includes(window.Telegram.WebApp.platform)) {
            this.addEventListener('click', this.handleTouchStart);
        } else {
            this.addEventListener('touchstart', this.handleTouchStart, { passive: false });
            this.addEventListener('touchmove', function (event) {
                event.stopPropagation();
                event.preventDefault();
            }, { passive: false });
        }
    }

    disconnectedCallback() {
        this.removeEventListener('click', this.handleTouchStart);
        this.removeEventListener('touchstart', this.handleTouchStart);
    }

    handleTouchStart = (event) => {
        event.preventDefault();
        const customEventName = this.events[this.eventIndex];
        const customEvent = new Event(customEventName, { bubbles: true });
        this.dispatchEvent(customEvent);
        this.eventIndex = (this.eventIndex + 1) % this.events.length;
        window.Telegram.WebApp.HapticFeedback.impactOccurred('heavy');
    }
}

customElements.define('touch-wrap', TouchWrap);