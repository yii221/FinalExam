<template>
    <div class="search-bar">
      <input
        type="text"
        v-model="query"
        placeholder="搜索商品..."
        @keyup.enter="search"
      />
      <button @click="search">
        <i>🔍</i>
      </button>
    </div>
  </template>
  
  <script>
  export default {
    name: 'SearchBar',
    data() {
      return {
        query: ''
      }
    },
    methods: {
      search() {
        this.$emit('search', this.query)
      }
    }
  }
  </script>
  
  <style scoped>
  .search-bar {
    display: flex;
    margin: 20px 0;
  }
  
  .search-bar input {
    flex: 1;
    padding: 10px 15px;
    border: 1px solid #ddd;
    border-radius: 4px 0 0 4px;
    font-size: 16px;
    outline: none;
  }
  
  .search-bar input:focus {
    border-color: #ff0036;
  }
  
  .search-bar button {
    padding: 0 15px;
    background-color: #ff0036;
    color: white;
    border: none;
    border-radius: 0 4px 4px 0;
    cursor: pointer;
    font-size: 16px;
  }
  
  .search-bar button:hover {
    background-color: #e60033;
  }
  </style>