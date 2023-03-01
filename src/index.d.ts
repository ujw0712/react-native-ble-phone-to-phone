export declare const advertiseStart: (uuid: string) => Promise<void>;
export declare const advertiseStop: () => Promise<void>;
export declare const scanStart: (uuids: string) => Promise<void>;
export declare const scanStop: () => Promise<void>;
export declare const BLEAdvertiser: any;

export declare type BleMobileInterface = {
  advertiseStart(uuid: string): Promise<void>;
  advertiseStop(): Promise<void>;
  scanStart(uuids: string): Promise<void>;
  scanStop(): Promise<void>;
};

