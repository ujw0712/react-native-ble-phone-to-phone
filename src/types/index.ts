export interface BleMobileInterface {
  advertiseStart(uuid?: string): Promise<void>;
  advertiseStop(): Promise<void>;
  scanStart(uuids?: string): Promise<void>;
  scanStop(): Promise<void>;
}
