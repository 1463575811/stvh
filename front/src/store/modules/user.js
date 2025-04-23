import { login, getInfo } from '@/api/user'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { resetRouter } from '@/router'

const getDefaultState = () => {
  return {
    token: getToken(),
    name: '',
    avatar: '',
    roles: [],
    menus: [],
    user: {}
  }
}

const state = getDefaultState()

const mutations = {
  RESET_STATE: (state) => {
    Object.assign(state, getDefaultState())
  },
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_NAME: (state, name) => {
    state.name = name
  },
  SET_AVATAR: (state, avatar) => {
    state.avatar = avatar
  },
  SET_ROLES: (state, roles) => {
    state.roles = roles
  },
  SET_MENUS: (state, menus) => {
    state.menus = menus
  },
  SET_USER: (state, user) => {
    state.user = user
  }
}

const actions = {
  // 用户登录
  login({ commit }, userInfo) {
    // 从表单取出用户名和密码
    const { username, password } = userInfo
    return new Promise((resolve, reject) => {
      // 调用后端登录接口
      login({ username: username.trim(), password: password }).then(response => {
        // 如果登录成功, 取出返回的用户信息
        const { data } = response
        // 将用户token存储在vuex中, 方便前端全局进行查询判断
        commit('SET_TOKEN', data.token) // 存储再vuex中
        // 将用户token存储在cookie中, 会话窗口结束就销毁cookie
        setToken(data.token)
        // 继续执行后续流程
        resolve()
      }).catch(error => {
        // 如果登录失败, 则由前端的统一异常处理器进行拦截, 并在页面显示错误信息
        reject(error)
      })
    })
  },

  // get user info
  getInfo({ commit, state }) {
    return new Promise((resolve, reject) => {
      getInfo(state.token).then(response => {
        const { data } = response

        if (!data) {
          return reject('用户信息过期, 请重新登录.')
        }

        const { username, avatar, roleNames } = data.user
        const { menuTree } = data

        const _user = data.user

        if (data.user.sex == 1) {
          _user.sex = '男'
        } else if (data.user.sex == 2) {
          _user.sex = '女'
        } else {
          _user.sex = '未设置'
        }

        commit('SET_NAME', username)
        commit('SET_AVATAR', avatar)
        commit('SET_ROLES', roleNames)
        commit('SET_MENUS', menuTree)
        commit('SET_USER', _user)

        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // user logout
  logout({ commit, state }) {
    return new Promise((resolve, reject) => {
      removeToken() // must remove  token  first
      resetRouter()
      commit('RESET_STATE')
      resolve()
    })
  },

  // remove token
  resetToken({ commit }) {
    return new Promise(resolve => {
      removeToken() // must remove  token  first
      commit('RESET_STATE')
      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}

