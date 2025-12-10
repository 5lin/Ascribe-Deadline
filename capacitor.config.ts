import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
    appId: 'com.countdown.app',
    appName: '倒计时笔记',
    webDir: 'dist',
    server: {
        androidScheme: 'https'
    }
};

export default config;
