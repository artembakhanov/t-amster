console.log(window.Telegram.WebApp.initDataUnsafe.user);

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
        this.addEventListener('touchstart', this.handleTouchStart);
    }

    disconnectedCallback() {
        this.removeEventListener('touchstart', this.handleTouchStart);
    }

    handleTouchStart = (event) => {
        const customEventName = this.events[this.eventIndex];
        const customEvent = new Event(customEventName, { bubbles: true });
        this.dispatchEvent(customEvent);
        this.eventIndex = (this.eventIndex + 1) % this.events.length;
        window.Telegram.WebApp.HapticFeedback.impactOccurred('heavy');
    }
}

customElements.define('touch-wrap', TouchWrap);