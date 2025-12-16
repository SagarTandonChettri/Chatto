

declare module "sockjs-client" {
  export default class SockJS{
    constructor(url: string, _?: unknown, options?: unknown);
    onopen: (() => void) | null;
    onmessage: ((event: { data: string }) => void) | null;
    onclose: (() => void) | null;
    close: () => void;
  }
}