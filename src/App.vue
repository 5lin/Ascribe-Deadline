<template>
  <div class="gradient-bg min-h-screen font-sans text-white overflow-x-hidden">
    <!-- 主界面 -->
    <div class="container mx-auto px-4 py-8 max-w-7xl">
      <!-- 顶部用户信息 -->
      <div class="flex justify-end mb-4">
        <div class="glass rounded-full px-4 py-2 flex items-center gap-3">
          <template v-if="currentUser">
            <span class="text-sm text-indigo-200">{{ currentUser.email }}</span>
            <button @click="logout" class="text-xs text-red-300 hover:text-red-200">退出</button>
          </template>
          <template v-else>
            <button @click="showLoginModal = true" class="text-sm text-indigo-200 hover:text-white">
              🔐 登录同步
            </button>
          </template>
        </div>
      </div>

      <div class="grid grid-cols-1 lg:grid-cols-2 gap-6 min-h-[85vh]">
        <!-- 左侧：倒计时卡片 -->
        <div class="glass rounded-3xl p-8 flex flex-col justify-between relative overflow-hidden">
          <div class="absolute -top-20 -right-20 w-60 h-60 bg-purple-500/20 rounded-full blur-3xl"></div>
          <div class="absolute -bottom-20 -left-20 w-60 h-60 bg-indigo-500/20 rounded-full blur-3xl"></div>

          <div class="relative z-10 flex justify-between items-start">
            <div>
              <div class="text-xs tracking-[0.2em] text-indigo-300 uppercase mb-2 font-semibold">
                {{ countdownTitle }}
              </div>
              <div class="text-3xl font-bold bg-gradient-to-r from-white to-indigo-200 bg-clip-text text-transparent">
                {{ targetDateDisplay }}
              </div>
            </div>
            <button @click="showSettings = true" class="p-2 hover:bg-white/10 rounded-lg transition" title="设置目标日期">
              <svg class="w-5 h-5 text-indigo-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2"
                  d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z" />
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" />
              </svg>
            </button>
          </div>

          <div class="relative z-10 py-12">
            <div class="grid grid-cols-4 gap-4">
              <div class="text-center" v-for="(unit, index) in timeUnits" :key="index">
                <div class="text-5xl lg:text-6xl font-extrabold number-gradient tabular-nums">
                  {{ timer[unit.key] }}
                </div>
                <div class="text-xs text-indigo-300 mt-2 tracking-wider uppercase font-medium">
                  {{ unit.label }}
                </div>
              </div>
            </div>
          </div>

          <div class="relative z-10">
            <div class="flex justify-between text-sm text-indigo-200 mb-3">
              <span>总体进度</span>
              <span class="font-semibold">{{ progress.toFixed(2) }}%</span>
            </div>
            <div class="h-3 bg-white/10 rounded-full overflow-hidden">
              <div class="h-full rounded-full transition-all duration-500 bg-gradient-to-r from-indigo-500 via-purple-500 to-pink-500"
                :style="{ width: progress + '%' }"></div>
            </div>
          </div>
        </div>

        <!-- 右侧：日历与笔记 -->
        <div class="glass-light rounded-3xl p-6 flex flex-col text-slate-800 overflow-hidden">
          <div class="flex justify-between items-center mb-6">
            <div class="flex items-center gap-3">
              <button @click="changeMonth(-1)"
                class="w-9 h-9 rounded-xl bg-slate-100 hover:bg-indigo-100 hover:text-indigo-600 transition flex items-center justify-center">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 19l-7-7 7-7" />
                </svg>
              </button>
              <span class="text-lg font-bold min-w-[140px] text-center">{{ monthDisplay }}</span>
              <button @click="changeMonth(1)"
                class="w-9 h-9 rounded-xl bg-slate-100 hover:bg-indigo-100 hover:text-indigo-600 transition flex items-center justify-center">
                <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
                </svg>
              </button>
            </div>
            <button @click="showTimeline = true"
              class="px-4 py-2 bg-gradient-to-r from-indigo-500 to-purple-500 text-white rounded-full text-sm font-semibold hover:shadow-lg hover:shadow-indigo-500/30 transition-all">
              📖 时光轴
            </button>
          </div>

          <div class="grid grid-cols-7 gap-1 mb-2">
            <div v-for="day in weekDays" :key="day" class="text-center text-xs text-slate-400 py-2 font-medium">
              {{ day }}
            </div>
          </div>
          <div class="grid grid-cols-7 gap-1 mb-4">
            <div v-for="(day, idx) in calendarDays" :key="idx" @click="day.date && selectDate(day.date)"
              class="aspect-square flex flex-col items-center justify-center rounded-xl text-sm cursor-pointer transition-all relative"
              :class="{
                'hover:bg-slate-100': day.date,
                'bg-gradient-to-br from-indigo-500 to-purple-500 text-white shadow-lg shadow-indigo-500/30': day.isToday,
                'ring-2 ring-indigo-400 bg-indigo-50': day.date === selectedDate && !day.isToday,
                'text-slate-300 cursor-default': !day.date
              }">
              <span>{{ day.day }}</span>
              <div v-if="notesMap[day.date]" class="w-1.5 h-1.5 rounded-full mt-1"
                :class="day.isToday ? 'bg-white' : 'bg-rose-400'"></div>
            </div>
          </div>

          <div class="flex-1 border-t border-slate-200 pt-4 flex flex-col min-h-0">
            <div class="flex justify-between items-center mb-3">
              <span class="text-sm font-semibold text-slate-600">
                {{ selectedDate || '选择日期' }}
              </span>
              <div class="flex items-center gap-2">
                <span class="text-xs" :class="saveStatus.includes('失败') ? 'text-red-500' : 'text-slate-400'">
                  {{ saveStatus }}
                </span>
                <span v-if="syncing"
                  class="w-4 h-4 border-2 border-indigo-500 border-t-transparent rounded-full animate-spin"></span>
              </div>
            </div>

            <!-- 已登录：显示编辑工具 -->
            <div v-if="currentUser" class="flex items-center gap-2 mb-3 flex-wrap">
              <div class="relative">
                <button @click="showEmojiPicker = !showEmojiPicker"
                  class="px-3 py-1.5 bg-slate-100 hover:bg-slate-200 rounded-lg text-sm transition">
                  😊 表情
                </button>
                <div v-if="showEmojiPicker"
                  class="absolute top-full left-0 mt-2 p-3 bg-white rounded-xl shadow-2xl border z-50 w-72">
                  <div class="grid grid-cols-8 gap-1">
                    <button v-for="emoji in emojis" :key="emoji" @click="insertEmoji(emoji)"
                      class="emoji-btn text-xl p-1.5 hover:bg-slate-100 rounded">
                      {{ emoji }}
                    </button>
                  </div>
                </div>
              </div>

              <label
                class="px-3 py-1.5 bg-slate-100 hover:bg-slate-200 rounded-lg text-sm cursor-pointer transition flex items-center gap-1">
                🖼️ 图片
                <input type="file" accept="image/*" @change="handleImageUpload" class="hidden" multiple>
              </label>
            </div>

            <!-- 图片预览 -->
            <div v-if="currentImages.length > 0" class="flex flex-wrap gap-2 mb-3">
              <div v-for="(img, idx) in currentImages" :key="idx" class="image-preview">
                <img :src="img.url" class="w-16 h-16 object-cover rounded-lg shadow">
                <button v-if="currentUser" @click="removeImage(idx)"
                  class="delete-btn w-5 h-5 bg-red-500 text-white rounded-full text-xs flex items-center justify-center shadow">×</button>
              </div>
            </div>

            <!-- 已登录：可编辑的文本框 -->
            <textarea v-if="currentUser" ref="noteInput" v-model="noteContent" @input="debounceSave"
              :disabled="!selectedDate" placeholder="在此记录今日的进度、感悟或待办事项... (自动保存)"
              class="flex-1 min-h-[120px] p-4 bg-slate-50 border border-slate-200 rounded-xl resize-none focus:ring-2 focus:ring-indigo-400 focus:border-transparent outline-none transition text-sm"></textarea>

            <!-- 未登录：只读显示 -->
            <div v-else class="flex-1 min-h-[120px] p-4 bg-slate-50 border border-slate-200 rounded-xl text-sm">
              <div v-if="noteContent" class="whitespace-pre-wrap text-slate-700">{{ noteContent }}</div>
              <div v-else class="text-slate-400 italic">暂无笔记</div>
              <div class="mt-4 text-center">
                <button @click="showLoginModal = true" class="text-indigo-500 hover:text-indigo-600 text-sm">
                  🔐 登录以编辑笔记
                </button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 时光轴侧边栏 -->
    <Transition name="slide">
      <div v-if="showTimeline" class="fixed inset-0 z-50 flex justify-end">
        <div @click="showTimeline = false" class="absolute inset-0 bg-black/50 backdrop-blur-sm"></div>

        <div class="relative w-full max-w-md bg-white h-full shadow-2xl flex flex-col">
          <div class="p-5 border-b flex justify-between items-center">
            <h2 class="text-lg font-bold text-slate-800">📖 笔记时光轴</h2>
            <button @click="showTimeline = false"
              class="w-8 h-8 flex items-center justify-center hover:bg-slate-100 rounded-lg text-xl text-slate-500">×</button>
          </div>

          <div class="flex-1 overflow-y-auto p-5 scrollbar-hide">
            <div v-if="allNotes.length === 0" class="text-center text-slate-400 mt-20">
              暂无笔记，快去记录吧！
            </div>
            <div v-else class="relative pl-5 border-l-2 border-indigo-100">
              <div v-for="note in allNotes" :key="note.id" class="mb-6 timeline-dot relative">
                <div class="text-xs text-indigo-500 font-semibold mb-1">{{ note.date }}</div>
                <div class="text-slate-700 text-sm whitespace-pre-wrap leading-relaxed">{{ note.content }}</div>
                <div v-if="note.images && note.images.length > 0" class="flex flex-wrap gap-2 mt-2">
                  <img v-for="(img, idx) in note.images" :key="idx" :src="img"
                    class="w-20 h-20 object-cover rounded-lg cursor-pointer hover:opacity-80 transition"
                    @click="previewImage(img)">
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </Transition>

    <!-- 图片预览 -->
    <Transition name="fade">
      <div v-if="previewImageUrl" @click="previewImageUrl = null"
        class="fixed inset-0 z-[100] bg-black/90 flex items-center justify-center p-8 cursor-zoom-out">
        <img :src="previewImageUrl" class="max-w-full max-h-full object-contain rounded-lg shadow-2xl">
      </div>
    </Transition>

    <!-- 状态指示器 -->
    <div class="fixed bottom-4 left-4 flex items-center gap-2 px-3 py-2 glass rounded-full text-xs">
      <div class="w-2 h-2 rounded-full" :class="connected ? 'bg-green-400' : 'bg-yellow-400'"></div>
      <span>{{ connected ? '云端同步' : '本地模式' }}</span>
    </div>

    <!-- 设置弹窗 -->
    <Transition name="fade">
      <div v-if="showSettings" class="fixed inset-0 z-[60] flex items-center justify-center p-4">
        <div @click="showSettings = false" class="absolute inset-0 bg-black/60 backdrop-blur-sm"></div>
        <div class="relative glass rounded-3xl p-6 w-full max-w-md">
          <h3 class="text-xl font-bold mb-6">⚙️ 倒计时设置</h3>

          <div class="space-y-4">
            <div>
              <label class="block text-sm text-indigo-200 mb-2">自定义标题 (留空则自动生成)</label>
              <input v-model="editGoalTitle" type="text" placeholder="例如：我的目标倒计时"
                class="w-full px-4 py-3 bg-white/10 border border-white/20 rounded-xl text-white placeholder-white/40 outline-none focus:border-indigo-400 transition">
            </div>

            <div>
              <label class="block text-sm text-indigo-200 mb-2">目标日期</label>
              <input v-model="editTargetDate" type="date"
                class="w-full px-4 py-3 bg-white/10 border border-white/20 rounded-xl text-white outline-none focus:border-indigo-400 transition">
            </div>

            <div>
              <label class="block text-sm text-indigo-200 mb-2">起始日期 (用于计算进度)</label>
              <input v-model="editStartDate" type="date"
                class="w-full px-4 py-3 bg-white/10 border border-white/20 rounded-xl text-white outline-none focus:border-indigo-400 transition">
            </div>
          </div>

          <div class="flex gap-3 mt-6">
            <button @click="showSettings = false"
              class="flex-1 py-3 bg-white/10 rounded-xl font-medium hover:bg-white/20 transition">
              取消
            </button>
            <button @click="saveSettings"
              class="flex-1 py-3 bg-gradient-to-r from-indigo-500 to-purple-500 rounded-xl font-semibold hover:shadow-lg transition">
              保存
            </button>
          </div>
        </div>
      </div>
    </Transition>

    <!-- 登录弹窗 -->
    <Transition name="fade">
      <div v-if="showLoginModal" class="fixed inset-0 z-[70] flex items-center justify-center p-4">
        <div @click="showLoginModal = false" class="absolute inset-0 bg-black/60 backdrop-blur-sm"></div>
        <div class="relative glass rounded-3xl p-6 w-full max-w-md">
          <div class="flex justify-between items-center mb-6">
            <h3 class="text-xl font-bold">🔐 登录账户</h3>
            <button @click="showLoginModal = false" class="text-white/50 hover:text-white text-xl">×</button>
          </div>

          <div v-if="authError" class="mb-4 p-3 bg-red-500/20 border border-red-500/30 rounded-xl text-red-200 text-sm">
            {{ authError }}
          </div>

          <div class="space-y-4">
            <div>
              <label class="block text-sm text-indigo-200 mb-2">邮箱</label>
              <input v-model="loginForm.email" type="email" placeholder="your@email.com"
                class="w-full px-4 py-3 bg-white/10 border border-white/20 rounded-xl text-white placeholder-white/40 outline-none focus:border-indigo-400 transition">
            </div>
            <div>
              <label class="block text-sm text-indigo-200 mb-2">密码</label>
              <input v-model="loginForm.password" type="password" placeholder="••••••••" @keyup.enter="login"
                class="w-full px-4 py-3 bg-white/10 border border-white/20 rounded-xl text-white placeholder-white/40 outline-none focus:border-indigo-400 transition">
            </div>
            <button @click="login" :disabled="authLoading"
              class="w-full py-3 bg-gradient-to-r from-indigo-500 to-purple-500 rounded-xl font-semibold hover:shadow-lg transition-all disabled:opacity-50">
              {{ authLoading ? '登录中...' : '登录' }}
            </button>
          </div>

          <div class="mt-4 pt-4 border-t border-white/10 text-center">
            <button @click="showRegister = !showRegister" class="text-indigo-400 hover:text-indigo-300 text-sm">
              {{ showRegister ? '返回登录' : '创建新账户' }}
            </button>
            <div v-if="showRegister" class="mt-4 space-y-3">
              <input v-model="registerForm.email" type="email" placeholder="邮箱"
                class="w-full px-4 py-3 bg-white/10 border border-white/20 rounded-xl text-white placeholder-white/40 outline-none focus:border-indigo-400 transition">
              <input v-model="registerForm.password" type="password" placeholder="密码 (至少8位)"
                class="w-full px-4 py-3 bg-white/10 border border-white/20 rounded-xl text-white placeholder-white/40 outline-none focus:border-indigo-400 transition">
              <input v-model="registerForm.passwordConfirm" type="password" placeholder="确认密码"
                class="w-full px-4 py-3 bg-white/10 border border-white/20 rounded-xl text-white placeholder-white/40 outline-none focus:border-indigo-400 transition">
              <button @click="register" :disabled="authLoading"
                class="w-full py-3 bg-gradient-to-r from-green-500 to-emerald-500 rounded-xl font-semibold hover:shadow-lg transition-all disabled:opacity-50">
                {{ authLoading ? '注册中...' : '注册' }}
              </button>
            </div>
          </div>
        </div>
      </div>
    </Transition>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import PocketBase from 'pocketbase'

const PB_URL = 'https://pb.l50.top'
const COLLECTION = 'notes'

const pb = new PocketBase(PB_URL)
const connected = ref(false)

const log = (msg) => {
  console.log(`[${new Date().toLocaleTimeString()}] ${msg}`)
}

// === 认证相关 ===
const isLoggedIn = ref(false)
const currentUser = ref(null)
const authLoading = ref(false)
const authError = ref('')
const showRegister = ref(false)
const showLoginModal = ref(false)
const loginForm = ref({ email: '', password: '' })
const registerForm = ref({ email: '', password: '', passwordConfirm: '' })

const login = async () => {
  authLoading.value = true
  authError.value = ''
  try {
    const authData = await pb.collection('users').authWithPassword(
      loginForm.value.email,
      loginForm.value.password
    )
    currentUser.value = authData.record
    isLoggedIn.value = true
    connected.value = true
    showLoginModal.value = false
    log('登录成功: ' + authData.record.email)
    await loadSettings()
    await loadAllNotes()
    selectDate(new Date().toISOString().split('T')[0])
  } catch (err) {
    log('登录失败: ' + err.message)
    authError.value = '登录失败: ' + (err.message || '请检查邮箱和密码')
  } finally {
    authLoading.value = false
  }
}

const register = async () => {
  if (registerForm.value.password !== registerForm.value.passwordConfirm) {
    authError.value = '两次密码不一致'
    return
  }
  if (registerForm.value.password.length < 8) {
    authError.value = '密码至少8位'
    return
  }

  authLoading.value = true
  authError.value = ''
  try {
    await pb.collection('users').create({
      email: registerForm.value.email,
      password: registerForm.value.password,
      passwordConfirm: registerForm.value.passwordConfirm,
    })
    log('注册成功')
    loginForm.value.email = registerForm.value.email
    loginForm.value.password = registerForm.value.password
    await login()
  } catch (err) {
    log('注册失败: ' + err.message)
    authError.value = '注册失败: ' + (err.message || '请稍后重试')
  } finally {
    authLoading.value = false
  }
}

const logout = () => {
  pb.authStore.clear()
  currentUser.value = null
  isLoggedIn.value = false
  connected.value = false
  notesMap.value = {}
  allNotes.value = []
  log('已退出登录')
}

// === 倒计时设置 ===
const showSettings = ref(false)
const targetDateStr = ref(localStorage.getItem('targetDate') || '2026-12-12')
const startDateStr = ref(localStorage.getItem('startDate') || '2025-01-01')
const goalTitle = ref(localStorage.getItem('goalTitle') || '')
const settingsId = ref(null)

const editTargetDate = ref('')
const editStartDate = ref('')
const editGoalTitle = ref('')

const loadSettings = async () => {
  try {
    const records = await pb.collection('settings').getList(1, 1, {
      sort: '-created'
    })

    if (records.items.length > 0) {
      const s = records.items[0]
      settingsId.value = s.id
      if (s.targetDate) targetDateStr.value = s.targetDate
      if (s.startDate) startDateStr.value = s.startDate
      if (s.goalTitle) goalTitle.value = s.goalTitle
      localStorage.setItem('targetDate', targetDateStr.value)
      localStorage.setItem('startDate', startDateStr.value)
      localStorage.setItem('goalTitle', goalTitle.value)
      log('已加载云端设置')
    } else {
      log('未找到云端设置，使用本地')
    }
  } catch (err) {
    log('加载设置: ' + err.message)
  }
}

const saveSettings = async () => {
  targetDateStr.value = editTargetDate.value
  startDateStr.value = editStartDate.value
  goalTitle.value = editGoalTitle.value

  localStorage.setItem('targetDate', targetDateStr.value)
  localStorage.setItem('startDate', startDateStr.value)
  localStorage.setItem('goalTitle', goalTitle.value)

  if (connected.value && currentUser.value) {
    try {
      const data = {
        user: currentUser.value.id,
        targetDate: targetDateStr.value,
        startDate: startDateStr.value,
        goalTitle: goalTitle.value
      }

      if (settingsId.value) {
        await pb.collection('settings').update(settingsId.value, data)
        log('设置已同步 (更新)')
      } else {
        const record = await pb.collection('settings').create(data)
        settingsId.value = record.id
        log('设置已同步 (创建)')
      }
    } catch (err) {
      log('同步设置失败: ' + err.message)
    }
  } else {
    log('设置已保存 (本地)')
  }

  showSettings.value = false
}

const countdownTitle = computed(() => {
  if (goalTitle.value) return goalTitle.value
  const year = targetDateStr.value.split('-')[0]
  return `Countdown To ${year}`
})

const targetDateDisplay = computed(() => {
  const [y, m, d] = targetDateStr.value.split('-')
  return `${y}年${parseInt(m)}月${parseInt(d)}日`
})

const timer = ref({ days: '000', hours: '00', mins: '00', secs: '00' })
const progress = ref(0)
const timeUnits = [
  { key: 'days', label: 'Days' },
  { key: 'hours', label: 'Hours' },
  { key: 'mins', label: 'Mins' },
  { key: 'secs', label: 'Secs' }
]

const pad = (n, len = 2) => String(n).padStart(len, '0')

const updateTimer = () => {
  const now = Date.now()
  const targetDate = new Date(targetDateStr.value + 'T00:00:00').getTime()
  const startDate = new Date(startDateStr.value + 'T00:00:00').getTime()
  const totalTime = targetDate - startDate
  const diff = targetDate - now

  if (diff < 0) {
    timer.value = { days: '0', hours: '00', mins: '00', secs: '00' }
    progress.value = 100
    return
  }

  timer.value = {
    days: String(Math.floor(diff / (1000 * 60 * 60 * 24))),
    hours: pad(Math.floor((diff % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))),
    mins: pad(Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))),
    secs: pad(Math.floor((diff % (1000 * 60)) / 1000))
  }

  let pct = ((now - startDate) / totalTime) * 100
  progress.value = Math.min(Math.max(pct, 0), 100)
}

// === 日历 ===
const currentNav = ref(new Date())
const selectedDate = ref(null)
const weekDays = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat']

const monthDisplay = computed(() => {
  const months = ["January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"]
  return `${months[currentNav.value.getMonth()]} ${currentNav.value.getFullYear()}`
})

const calendarDays = computed(() => {
  const year = currentNav.value.getFullYear()
  const month = currentNav.value.getMonth()
  const firstDay = new Date(year, month, 1).getDay()
  const daysInMonth = new Date(year, month + 1, 0).getDate()
  const today = new Date().toISOString().split('T')[0]

  const days = []
  for (let i = 0; i < firstDay; i++) {
    days.push({ day: '', date: null })
  }
  for (let i = 1; i <= daysInMonth; i++) {
    const dateStr = `${year}-${pad(month + 1)}-${pad(i)}`
    days.push({
      day: i,
      date: dateStr,
      isToday: dateStr === today
    })
  }
  return days
})

const changeMonth = (delta) => {
  const newDate = new Date(currentNav.value)
  newDate.setMonth(newDate.getMonth() + delta)
  currentNav.value = newDate
}

// === 笔记 ===
const noteContent = ref('')
const currentImages = ref([])
const saveStatus = ref('')
const syncing = ref(false)
const notesMap = ref({})
const allNotes = ref([])
const noteInput = ref(null)

const showEmojiPicker = ref(false)
const emojis = [
  '😀', '😃', '😄', '😁', '😅', '😂', '🤣', '😊',
  '😇', '🥰', '😍', '🤩', '😘', '😗', '😚', '😙',
  '🥲', '😋', '😛', '😜', '🤪', '😝', '🤑', '🤗',
  '❤️', '🧡', '💛', '💚', '💙', '💜', '🖤', '🤍',
  '💯', '✨', '🔥', '⭐', '🌟', '💫', '🎉', '🎊',
  '👍', '👎', '👏', '🙌', '🤝', '💪', '✌️', '🤞',
  '📝', '📚', '💡', '🎯', '🚀', '💻', '☕', '🍵'
]

const insertEmoji = (emoji) => {
  noteContent.value += emoji
  showEmojiPicker.value = false
  debounceSave()
}

const handleImageUpload = async (e) => {
  const files = Array.from(e.target.files)
  for (const file of files) {
    const url = URL.createObjectURL(file)
    currentImages.value.push({ file, url })
  }
  e.target.value = ''
  debounceSave()
}

const removeImage = (idx) => {
  URL.revokeObjectURL(currentImages.value[idx].url)
  currentImages.value.splice(idx, 1)
  debounceSave()
}

const selectDate = async (dateStr) => {
  selectedDate.value = dateStr
  currentImages.value = []
  saveStatus.value = ''

  // 始终尝试从云端加载（公开读取）
  try {
    log('加载笔记: ' + dateStr)
    const records = await pb.collection(COLLECTION).getList(1, 1, {
      filter: `date = "${dateStr}"`
    })

    if (records.items.length > 0) {
      const note = records.items[0]
      noteContent.value = note.content || ''
      notesMap.value[dateStr] = note.id

      if (note.media && note.media.length > 0) {
        currentImages.value = note.media.map(filename => ({
          file: null,
          url: pb.files.getURL(note, filename),
          existing: filename
        }))
      }
      log('已加载笔记')
    } else {
      noteContent.value = ''
      delete notesMap.value[dateStr]
      // 如果云端没有，尝试本地
      const localNote = localStorage.getItem('note_' + dateStr)
      if (localNote) {
        noteContent.value = localNote
        notesMap.value[dateStr] = true
      }
    }
  } catch (err) {
    log('云端加载失败，使用本地: ' + err.message)
    // 云端失败，回退到本地
    const localNote = localStorage.getItem('note_' + dateStr)
    noteContent.value = localNote || ''
    if (localNote) {
      notesMap.value[dateStr] = true
    }
  }
}

let saveTimeout = null
const debounceSave = () => {
  if (!selectedDate.value) return
  saveStatus.value = '编辑中...'
  clearTimeout(saveTimeout)
  saveTimeout = setTimeout(saveNote, 800)
}

const saveNote = async () => {
  if (!selectedDate.value) return

  syncing.value = true
  saveStatus.value = '保存中...'

  if (connected.value) {
    try {
      const formData = new FormData()
      formData.append('date', selectedDate.value)
      formData.append('content', noteContent.value)
      formData.append('user', currentUser.value.id)

      const existingFilesToKeep = []
      for (const img of currentImages.value) {
        if (img.file) {
          formData.append('media', img.file)
        } else if (img.existing) {
          existingFilesToKeep.push(img.existing)
        }
      }

      if (existingFilesToKeep.length > 0) {
        for (const filename of existingFilesToKeep) {
          formData.append('media', filename)
        }
      } else if (currentImages.value.filter(i => i.file).length === 0) {
        formData.append('media', '')
      }

      const existingId = notesMap.value[selectedDate.value]

      if (noteContent.value.trim() || currentImages.value.length > 0) {
        let record
        if (existingId) {
          record = await pb.collection(COLLECTION).update(existingId, formData)
          log('更新成功: ' + existingId)
        } else {
          record = await pb.collection(COLLECTION).create(formData)
          log('创建成功: ' + record.id)
        }
        notesMap.value[selectedDate.value] = record.id
        saveStatus.value = '已同步 ✓'
      } else if (existingId) {
        await pb.collection(COLLECTION).delete(existingId)
        delete notesMap.value[selectedDate.value]
        saveStatus.value = '已删除'
        log('删除成功')
      } else {
        saveStatus.value = ''
      }

      await loadAllNotes()
    } catch (err) {
      log('保存失败: ' + err.message)
      saveStatus.value = '保存失败 ✗'
    }
  } else {
    if (noteContent.value.trim()) {
      localStorage.setItem('note_' + selectedDate.value, noteContent.value)
      notesMap.value[selectedDate.value] = true
    } else {
      localStorage.removeItem('note_' + selectedDate.value)
      delete notesMap.value[selectedDate.value]
    }
    saveStatus.value = '已保存 (本地)'
    loadLocalNotes()
  }

  syncing.value = false
}

const loadAllNotes = async () => {
  try {
    const records = await pb.collection(COLLECTION).getFullList({
      sort: '-date'
    })

    allNotes.value = records.map(r => ({
      id: r.id,
      date: r.date,
      content: r.content,
      images: r.media ? r.media.map(f => pb.files.getURL(r, f)) : []
    }))

    notesMap.value = {}
    records.forEach(r => {
      notesMap.value[r.date] = r.id
    })

    connected.value = true
    log('已加载 ' + records.length + ' 条笔记')
  } catch (err) {
    log('加载笔记: ' + err.message)
    loadLocalNotes()
  }
}

const loadLocalNotes = () => {
  const notes = []
  notesMap.value = {}
  for (let i = 0; i < localStorage.length; i++) {
    const key = localStorage.key(i)
    if (key.startsWith('note_')) {
      const date = key.replace('note_', '')
      notes.push({
        id: date,
        date: date,
        content: localStorage.getItem(key),
        images: []
      })
      notesMap.value[date] = true
    }
  }
  notes.sort((a, b) => new Date(b.date) - new Date(a.date))
  allNotes.value = notes
}

// === 时光轴 ===
const showTimeline = ref(false)
const previewImageUrl = ref(null)

const previewImage = (url) => {
  previewImageUrl.value = url
}

// === 初始化 ===
onMounted(async () => {
  updateTimer()
  setInterval(updateTimer, 1000)

  // 初始化编辑值
  editTargetDate.value = targetDateStr.value
  editStartDate.value = startDateStr.value
  editGoalTitle.value = goalTitle.value

  // 先检查登录状态
  if (pb.authStore.isValid) {
    currentUser.value = pb.authStore.model
    isLoggedIn.value = true
    connected.value = true
    log('已恢复登录: ' + currentUser.value.email)
  }

  // 先加载设置和所有笔记
  await loadSettings()
  await loadAllNotes()
  
  // 最后选择今天的日期
  selectDate(new Date().toISOString().split('T')[0])

  document.addEventListener('click', (e) => {
    if (!e.target.closest('.relative')) {
      showEmojiPicker.value = false
    }
  })
})
</script>
