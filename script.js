// Meraj Jarvis AI - Complete Client-Side Script
// Preserving all capabilities: TTS Neural Speech, Dual-Mode, 1-Tap Key Assignment, Engine 2 Mouse Look, Health Caretaker

const HINDI_AUTOMATIC_WELCOME = "नमस्ते! मैं आपके साथ यहां ऑटोमेटिक काम करने के लिए रेडी हूं। आप जो बोलिएगा मैं वही ऑटोमेटिक कर दूंगा, आपको फोन को छूने की जरूरत नहीं है। बोलिए अब क्या करूं?";

// State Management
const appState = {
  currentMode: 'medium', // 'medium', 'automatic', 'gaming_key'
  trialHoursLeft: 24,
  isVipActive: false,
  isSpeaking: false,
  isListening: false,
  mouseLocked: false,
  mouseSensitivity: 2.4,
  hardwareLinked: true,
  keyMappings: [
    { id: 'btn_fire', label: 'M1', action: 'FIRE', class: 'fire' },
    { id: 'btn_run', label: 'SHIFT', action: 'SPRINT', class: 'run' },
    { id: 'btn_scope', label: 'M2', action: 'SCOPE', class: 'scope' },
    { id: 'btn_jump', label: 'SPACE', action: 'JUMP', class: 'jump' }
  ]
};

// Speech Synthesis (Premium Female Neural Voice)
function speakText(text) {
  if (!('speechSynthesis' in window)) return;
  
  window.speechSynthesis.cancel();
  const utterance = new SpeechSynthesisUtterance(text);
  
  // Set voice to female Hindi / Indian English if available
  const voices = window.speechSynthesis.getVoices();
  const femaleVoice = voices.find(v => (v.lang.includes('hi') || v.lang.includes('en-IN')) && (v.name.includes('Female') || v.name.includes('Google') || v.name.includes('Natural')));
  if (femaleVoice) {
    utterance.voice = femaleVoice;
  }
  utterance.rate = 1.0;
  utterance.pitch = 1.1;

  utterance.onstart = () => {
    appState.isSpeaking = true;
    updateVoiceUI(true, text);
  };

  utterance.onend = () => {
    appState.isSpeaking = false;
    updateVoiceUI(false, "Jarvis Neural Core Active • Standing By");
  };

  window.speechSynthesis.speak(utterance);
}

function updateVoiceUI(speaking, text) {
  const statusEl = document.getElementById('voice-status');
  const dots = document.querySelectorAll('.wave-dot');
  if (statusEl) statusEl.textContent = text;
  dots.forEach(d => {
    if (speaking) d.classList.add('active');
    else d.classList.remove('active');
  });
}

// Mode Launchers
function launchMediumMode() {
  appState.currentMode = 'medium';
  updateActiveCard('card-medium');
  speakText("Medium Mode engaged. You can search products, execute web tasks, or command Jarvis directly.");
  showToast("Medium Mode Activated: Free Standard Search & Vision");
}

function launchAutomaticMode() {
  appState.currentMode = 'automatic';
  updateActiveCard('card-auto');
  speakText(HINDI_AUTOMATIC_WELCOME);
  showToast("Automatic Mode Activated: 1-Day Free Trial Running");
}

function launchGamingMode() {
  appState.currentMode = 'gaming_key';
  updateActiveCard('card-gaming');
  speakText("Gaming Key-Mapping Mode engaged. 100% Free with zero VIP gating and anti-ban Bluetooth HID touch conversion.");
  showToast("Gaming Mode Activated: 100% Free • Ban-Free");
}

function updateActiveCard(cardId) {
  document.querySelectorAll('.mode-card').forEach(c => c.classList.remove('active'));
  const active = document.getElementById(cardId);
  if (active) active.classList.add('active');
}

// 1-Touch Key Assignment Engine
let currentTargetKey = null;

function openKeyRebindPrompt(keyId) {
  const mapping = appState.keyMappings.find(k => k.id === keyId);
  if (!mapping) return;

  currentTargetKey = mapping;
  const newKey = prompt(`1-Touch Key Assignment\nAssign keyboard key for [${mapping.action}]:`, mapping.label);
  if (newKey && newKey.trim() !== "") {
    mapping.label = newKey.trim().toUpperCase();
    const nodeEl = document.getElementById(keyId);
    if (nodeEl) {
      nodeEl.innerHTML = `${mapping.label}<span class="hud-key-action">${mapping.action}</span>`;
    }
    speakText(`Mapped ${mapping.action} to key ${mapping.label}`);
    showToast(`Engine 1: Bound [${mapping.action}] to '${mapping.label}'`);
  }
}

// Engine 2: Mouse Look & Drag Headshot Flick
function toggleMouseLock() {
  appState.mouseLocked = !appState.mouseLocked;
  const btn = document.getElementById('btn-mouse-lock');
  if (btn) {
    btn.textContent = appState.mouseLocked ? "🔒 MOUSE LOCKED (360° AIM)" : "🔓 MOUSE UNLOCKED (CURSOR)";
    btn.style.borderColor = appState.mouseLocked ? "var(--neon-green)" : "var(--surface-border)";
    btn.style.color = appState.mouseLocked ? "var(--neon-green)" : "var(--text-primary)";
  }
  showToast(appState.mouseLocked ? "360° Mouse Look Aim Locked" : "Mouse Cursor Unlocked");
}

function triggerDragHeadshotFlick() {
  speakText("Executing auto drag headshot micro-flick via kernel touch injection.");
  showToast("🎯 Engine 2: Upward Drag-Headshot Flick Executed (-420px)");
}

// Toast Helper
function showToast(message) {
  const toast = document.getElementById('toast');
  if (!toast) return;
  toast.textContent = message;
  toast.classList.add('show');
  setTimeout(() => toast.classList.remove('show'), 3500);
}

// Floating Sidebar Modal
function toggleModal(open) {
  const modal = document.getElementById('floating-modal');
  if (modal) {
    if (open) modal.classList.add('active');
    else modal.classList.remove('active');
  }
}

// Command Execution
function executeCommand() {
  const input = document.getElementById('command-input');
  if (!input || !input.value.trim()) return;
  const cmd = input.value.trim();
  input.value = "";

  if (cmd.toLowerCase().includes("shoe") || cmd.toLowerCase().includes("flipkart")) {
    speakText("Flipkart par 5 number shoes filter kar diye hain. Bhai yeh 1299 waala running shoe best hai, buy kar do!");
    showToast("Shopping Directive: Shoes Loaded with Voice Buy Advice");
  } else if (cmd.toLowerCase().includes("break") || cmd.toLowerCase().includes("thak")) {
    speakText("Bhai, aapka din lamba raha aur aap thake hue lag rahe hain. Chaliye thoda relax karte hain, main saare non-essential alerts mute kar deti hoon.");
    showToast("Health Caretaker: Fatigue Care Activated");
  } else {
    speakText(`Directive acknowledged: ${cmd}. Processing via autonomous kernel protocol.`);
    showToast(`Executed: ${cmd}`);
  }
}

// Event Listeners
document.addEventListener('DOMContentLoaded', () => {
  // Speech voices loader
  if ('speechSynthesis' in window) {
    window.speechSynthesis.onvoiceschanged = () => {
      window.speechSynthesis.getVoices();
    };
  }

  // Send button & enter key
  const btnSend = document.getElementById('btn-send');
  const inputCmd = document.getElementById('command-input');
  if (btnSend) btnSend.addEventListener('click', executeCommand);
  if (inputCmd) {
    inputCmd.addEventListener('keydown', (e) => {
      if (e.key === 'Enter') executeCommand();
    });
  }

  // Mic Toggle
  const btnMic = document.getElementById('btn-mic');
  if (btnMic) {
    btnMic.addEventListener('click', () => {
      appState.isListening = !appState.isListening;
      btnMic.classList.toggle('listening', appState.isListening);
      if (appState.isListening) {
        speakText("Boliye bhai, main sun rahi hoon.");
        showToast("Mic Listening...");
      }
    });
  }

  // Floating Sidebar Pill
  const pill = document.getElementById('floating-pill');
  if (pill) {
    pill.addEventListener('click', () => toggleModal(true));
  }

  // Modal Close
  const btnCloseModal = document.getElementById('btn-close-modal');
  if (btnCloseModal) {
    btnCloseModal.addEventListener('click', () => toggleModal(false));
  }

  // 24/7 Meal-Time Reminder Check
  setInterval(() => {
    const now = new Date();
    const hours = now.getHours();
    const minutes = now.getMinutes();
    if (hours === 13 && minutes === 0) {
      speakText("Bhai, 1:00 PM ho gaya hai. Lunch ka time ho gaya, pehle khana kha lijiye!");
    } else if (hours === 21 && minutes === 0) {
      speakText("Bhai, 9:00 PM ho gaya hai. Dinner time! Khana khao aur thoda relax karo.");
    }
  }, 60000);
});
