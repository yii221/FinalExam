import axios from 'axios'

export const register = (formData) => {
  return axios.post('http://localhost:8080/TradingSystem/register', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  }).then(res => res.data)
}