import axios from 'axios';

const ip = window.location.host;
const API_BASE_URL = `http://${ip}:8080`;
const AppService = {
    getAllApps: async () => {
        try {
            const response = await axios.get(`${API_BASE_URL}/apps`);
            return response.data;
        } catch (error) {
            console.log('Error fetching apps:'+ error);
            return [];
        }
    },

    addApp: async (newApp) => {
        try {
            const response = await axios.post(`${API_BASE_URL}/apps`, newApp);
            return response.data;
        } catch (error) {
            console.log('Error fetching apps:'+ error);
        }
    },

    deleteApp: async (appId) => {
        try {
            await axios.delete(`${API_BASE_URL}/apps/${appId}`);
        } catch (error) {
            console.log('Error fetching apps:'+ error);
        }
    },
};

export default AppService;
