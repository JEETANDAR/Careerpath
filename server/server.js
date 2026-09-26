const http = require('http');
const fs = require('fs');
const path = require('path');

const PORT = 3000;

function getApiKey() {
  if (process.env.GEMINI_API_KEY && process.env.GEMINI_API_KEY !== 'MY_GEMINI_API_KEY') {
    return process.env.GEMINI_API_KEY.trim();
  }
  // Try /app/.dev.env.json
  try {
    const devEnvPath = '/app/.dev.env.json';
    if (fs.existsSync(devEnvPath)) {
      const parsed = JSON.parse(fs.readFileSync(devEnvPath, 'utf8'));
      if (parsed.GEMINI_API_KEY && parsed.GEMINI_API_KEY !== 'MY_GEMINI_API_KEY') {
        return parsed.GEMINI_API_KEY.trim();
      }
    }
  } catch (e) {}

  // Try .env in root or current dir
  try {
    const envPaths = [path.resolve(__dirname, '../.env'), path.resolve(__dirname, '.env'), '/app/applet/.env', '/app/.env'];
    for (const p of envPaths) {
      if (fs.existsSync(p)) {
        const lines = fs.readFileSync(p, 'utf8').split('\n');
        for (const line of lines) {
          const trimmed = line.trim();
          if (trimmed.startsWith('GEMINI_API_KEY=') && !trimmed.startsWith('#')) {
            const val = trimmed.split('=')[1].trim();
            if (val && val !== 'MY_GEMINI_API_KEY') return val;
          }
        }
      }
    }
  } catch (e) {}

  return '';
}

function cleanMarkdown(text) {
  return text
    .replace(/\*\*/g, '')
    .replace(/\*/g, '')
    .replace(/`/g, '')
    .replace(/##/g, '')
    .replace(/#/g, '')
    .replace(/___/g, '')
    .replace(/__/g, '')
    .trim();
}

function shouldUseGrounding(userMessage) {
  if (!userMessage) return false;
  const msg = userMessage.trim().toLowerCase();

  // Basic greetings & pleasantries that NEVER need web search
  if (msg.length <= 25 && /^(hi|hello|hey|hola|namaste|namaskara|namaskar|good\s|who|thanks|thank you|ok|okay|help|bye|ಕನ್ನಡ|ಹಲೋ|ನಮಸ್ಕಾರ)/i.test(msg)) {
    return false;
  }

  const commonConversational = [
    'who are you', 'what is your name', 'what can you do', 'how can you help',
    'tell me about yourself', 'ಯಾರು ನೀವು', 'ನಿಮ್ಮ ಹೆಸರೇನು', 'ಧನ್ಯವಾದ', 'thank you', 'thanks'
  ];
  if (commonConversational.some(c => msg.includes(c))) {
    return false;
  }

  // Keywords indicating live cutoffs, dates, fees, rules, exams, admissions
  const searchSignals = [
    'cutoff', 'cut off', 'cut-off', 'fee', 'fees', 'cost', 'date', 'dates', 'deadline',
    'schedule', 'ranking', 'rank', 'nirf', 'exam', 'entrance', 'admit card', 'result',
    'syllabus', 'seat matrix', 'counselling', 'counseling', 'neet', 'jee', 'kcet',
    'comedk', 'cuet', 'clat', 'cat', 'gate', 'iat', 'nest', 'scholarship', 'ssp',
    'nsp', 'inspire', 'latest', '2024', '2025', '2026', 'update', 'news', 'admission',
    'application form', 'apply', 'verification', 'document',
    'ಕಟ್‌ಆಫ್', 'ಶುಲ್ಕ', 'ದಿನಾಂಕ', 'ಪರೀಕ್ಷೆ', 'ಪ್ರವೇಶ', 'ಶ್ರೇಣಿ'
  ];

  return searchSignals.some(s => msg.includes(s));
}

async function callGemini(apiKey, userMessage, studentName, studentStream, isKannada, history) {
  const models = ['gemini-3.5-flash-lite', 'gemini-3.5-flash', 'gemini-flash-latest'];
  const needsGrounding = shouldUseGrounding(userMessage);

  const languageDirective = isKannada
    ? "CRITICAL REQUIREMENT: The student has selected KANNADA (ಕನ್ನಡ) language. You MUST generate your ENTIRE response in natural, fluent, grammatically accurate Kannada script (ಕನ್ನಡ ಲಿಪಿಯಲ್ಲೇ ಉತ್ತರಿಸಿ). DO NOT answer in English. Even if search results or user questions are in English, translate and explain everything completely in Kannada. Use clear Kannada terminology and bullet points."
    : "If the student asks in Kannada, respond in Kannada. Otherwise respond in clear, encouraging, student-friendly English.";

  const systemInstructionText = `You are Vidyabot, a friendly, encouraging, and highly knowledgeable AI Career & College Companion for Indian students in CareerPath.
The student speaking with you is ${studentName} (${studentStream}).
${languageDirective}

Core Principles:
1. GROUNDING & ACCURACY: When answering questions regarding career pathways (e.g. becoming a scientist, doctor, engineer, civil servant), college admissions, entrance exams (NEET, JEE, IAT, NEST, KCET), cutoffs, fee structures, NIRF rankings, or counselling rules, provide up-to-date accurate facts.
2. PREFER OFFICIAL SOURCES: Always prioritize authoritative bodies:
   - Science/Research: IISc, IISERs, NISER, ISRO, DRDO, CSIR, DST, BARC
   - Testing/Counselling: NTA (nta.ac.in), KEA (kea.kar.nic.in), JoSAA/CSAB (josaa.nic.in), MCC (mcc.nic.in)
   - Accreditations: AICTE, UGC, NMC
   - Scholarships: SSP Karnataka (ssp.postmatric.karnataka.gov.in), National Scholarship Portal (scholarships.gov.in), INSPIRE SHE
   Always explicitly cite the official portal or conducting body.
3. STUDENT-FRIENDLY TONE: Keep answers simple, warm, supportive, step-by-step, and free of dense bureaucratic jargon.
4. CLEAN FORMATTING: Use clean bullet points (•) and relevant emojis. Do NOT use markdown bold asterisks (**) or hashes (#).`;

  const contents = [];
  if (Array.isArray(history)) {
    for (const item of history.slice(-6)) {
      contents.push({
        role: item.isUser ? 'user' : 'model',
        parts: [{ text: item.text }]
      });
    }
  }

  const promptText = isKannada
    ? `${userMessage}\n\n(ದಯವಿಟ್ಟು ಸಂಪೂರ್ಣ ವಿವರಣೆಯನ್ನು ಕಡ್ಡಾಯವಾಗಿ ಕನ್ನಡದಲ್ಲೇ [ಕನ್ನಡ ಲಿಪಿ] ನೀಡಿ. Reply strictly in Kannada.)`
    : userMessage;

  contents.push({
    role: 'user',
    parts: [{ text: promptText }]
  });

  for (const model of models) {
    try {
      const url = `https://generativelanguage.googleapis.com/v1beta/models/${model}:generateContent?key=${apiKey}`;

      // 1. If query requires grounding, attempt with Google Search Grounding first (with 8s timeout)
      if (needsGrounding) {
        try {
          const payloadGrounding = {
            contents: contents,
            systemInstruction: { parts: [{ text: systemInstructionText }] },
            tools: [{ googleSearch: {} }],
            generationConfig: { temperature: 0.7, topP: 0.95 }
          };

          const response = await fetch(url, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payloadGrounding),
            signal: AbortSignal.timeout(8000)
          });

          if (response.ok) {
            const data = await response.json();
            const candidate = data.candidates?.[0];
            const text = candidate?.content?.parts?.[0]?.text;
            if (text) {
              const sources = [];
              const chunks = candidate.groundingMetadata?.groundingChunks;
              if (Array.isArray(chunks)) {
                const seen = new Set();
                for (const chunk of chunks) {
                  const uri = chunk.web?.uri?.trim();
                  const title = chunk.web?.title?.trim() || uri;
                  if (uri && !seen.has(uri)) {
                    seen.add(uri);
                    sources.push({ title, url: uri });
                  }
                }
              }
              return { replyText: cleanMarkdown(text), sources };
            }
          }
        } catch (groundingErr) {
          console.warn(`Grounding for ${model} timed out or failed:`, groundingErr.message);
        }
      }

      // 2. Fast standard generation (without tools) - completes in ~500ms to 1.5s
      const payloadStandard = {
        contents: contents,
        systemInstruction: { parts: [{ text: systemInstructionText }] },
        generationConfig: { temperature: 0.7, topP: 0.95 }
      };

      const stdRes = await fetch(url, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payloadStandard),
        signal: AbortSignal.timeout(6000)
      });

      if (stdRes.ok) {
        const stdData = await stdRes.json();
        const cand = stdData.candidates?.[0];
        const text = cand?.content?.parts?.[0]?.text;
        if (text) {
          return { replyText: cleanMarkdown(text), sources: [] };
        }
      }
    } catch (err) {
      console.warn(`Error calling Gemini ${model}:`, err.message);
    }
  }

  return null;
}

// Fallback intelligent career advisor for when API key is missing or network fails
function generateIntelligentFallback(query, studentName, studentStream, isKannada) {
  const q = (query || '').toLowerCase();

  if (isKannada) {
    if (q.includes('scientist') || q.includes('ವಿಜ್ಞಾನಿ') || q.includes('research') || q.includes('ಸಂಶೋಧನೆ') || q.includes('isro') || q.includes('drdo') || q.includes('barc')) {
      return {
        replyText: `🚀 ವಿಜ್ಞಾನಿ (Scientist / Researcher) ಆಗುವ ಸಂಪೂರ್ಣ ಮಾರ್ಗಸೂಚಿ:

• 1. ಮೂಲಭೂತ ಅರ್ಹತೆ:
  10+2 ತರಗತಿಯಲ್ಲಿ ವಿಜ್ಞಾನ ವಿಭಾಗ (PCM ಅಥವಾ PCB) ಕನಿಷ್ಠ 60%+ ಅಂಕಗಳೊಂದಿಗೆ ಪೂರ್ಣಗೊಳಿಸಿ.

• 2. ಭಾರತದ ಪ್ರಮುಖ ಸಂಶೋಧನಾ ಸಂಸ್ಥೆಗಳು:
  • IISc ಬೆಂಗಳೂರು (ಭಾರತದ ನಂ.1 ವಿಜ್ಞಾನ ಸಂಸ್ಥೆ)
  • IISERs (ಪುಣೆ, ಮೊಹಾಲಿ, ಕೋಲ್ಕತ್ತಾ, ಭೋಪಾಲ್, ತಿರುವನಂತಪುರಂ, ತಿರುಪತಿ, ಬೆಹ್ರಾಂಪುರ)
  • NISER ಭುವನೇಶ್ವರ ಮತ್ತು UM-DAE CEBS ಮುಂಬೈ
  • IIT ಗಳು (BS ಮತ್ತು ಇಂಟಿಗ್ರೇಟೆಡ್ MSc)

• 3. ಪ್ರಮುಖ ಪ್ರವೇಶ ಪರೀಕ್ಷೆಗಳು:
  • IAT (IISER Aptitude Test): IISER ಗಳಲ್ಲಿ 5-ವರ್ಷಗಳ BS-MS ಕೋರ್ಸ್‌ಗೆ ಪ್ರವೇಶ.
  • NEST (National Entrance Screening Test): NISER & CEBS ಗಾಗಿ.
  • JEE Advanced: IISc Bangalore ನ 4-ವರ್ಷಗಳ Bachelor of Science (BS) ಕೋರ್ಸ್‌ಗೆ.
  • CUET-UG: ಕೇಂದ್ರ ವಿಶ್ವವಿದ್ಯಾಲಯಗಳಲ್ಲಿ BSc (Hons) ಗಾಗಿ.

• 4. ಶೈಕ್ಷಣಿಕ ಹಂತಗಳು:
  12ನೇ ತರಗತಿ ➔ 4-ವರ್ಷದ BS ಅಥವಾ 5-ವರ್ಷದ BS-MS ➔ ಸ್ನಾತಕೋತ್ತರ ಪದವಿ (MSc/M.Tech) ➔ Ph.D. (ಡಾಕ್ಟರೇಟ್ ಸಂಶೋಧನೆ).

• 5. ವಿಜ್ಞಾನಿಗಳಿಗೆ ಉದ್ಯೋಗಾವಕಾಶಗಳು:
  • ISRO (ಭಾರತೀಯ ಬಾಹ್ಯಾಕಾಶ ಸಂಶೋಧನಾ ಸಂಸ್ಥೆ - ICRB ಪರೀಕ್ಷೆ)
  • DRDO (ರಕ್ಷಣಾ ಸಂಶೋಧನೆ - Scientist 'B' ಪೋಸ್ಟ್)
  • BARC & DAE (ಪರಮಾಣು ಸಂಶೋಧನಾ ಕೇಂದ್ರ - OCES/DGFS ಯೋಜನೆ)
  • CSIR ಪ್ರಯೋಗಾಲಯಗಳು & TIFR

• 6. ವಿದ್ಯಾರ್ಥಿವೇತನಗಳು:
  • INSPIRE SHE ಸ್ಕಾಲರ್‌ಶಿಪ್ (DST): ವರ್ಷಕ್ಕೆ ₹80,000 ಆರ್ಥಿಕ ನೆರವು.
  • CSIR-NET / UGC-JRF: Ph.D. ಅಧ್ಯಯನಕ್ಕಾಗಿ ಮಾಸಿಕ ₹37,000+ ಫೆಲೋಶಿಪ್.

💡 ಸಲಹೆ: ಅಧಿಕೃತ ವೆಬ್‌ಸೈಟ್‌ಗಳಾದ iiseradmission.in ಮತ್ತು niser.ac.in ಮೂಲಕ ಅರ್ಜಿ ನಮೂನೆಗಳನ್ನು ಗಮನಿಸುತ್ತಿರಿ!`,
        sources: [
          { title: "IISER Admissions Portal", url: "https://iiseradmission.in" },
          { title: "National Entrance Screening Test (NEST)", url: "https://www.nestexam.in" },
          { title: "IISc Bangalore UG Admissions", url: "https://admissions.iisc.ac.in" }
        ]
      };
    }

    if (q.includes('bds') || q.includes('dental') || q.includes('ದಂತ')) {
      return {
        replyText: `📌 BDS (ದಂತ ವೈದ್ಯಕೀಯ ಶಿಕ್ಷಣ) ಸಂಪೂರ್ಣ ಮಾಹಿತಿ:

• ಅರ್ಹತೆ: 12ನೇ ತರಗತಿ PCB 50%+ ಮತ್ತು NEET-UG ಪ್ರವೇಶ ಪರೀಕ್ಷೆ (NTA).
• ಅಧಿಕೃತ ಕೌನ್ಸೆಲಿಂಗ್: ಕರ್ನಾಟಕದಲ್ಲಿ KEA (kea.kar.nic.in) ಮತ್ತು ಆಲ್ ಇಂಡಿಯಾ ಕೋಟಾ MCC (mcc.nic.in).
• ಅವಧಿ: 5 ವರ್ಷಗಳು (4 ವರ್ಷಗಳ ಕೋರ್ಸ್ + 1 ವರ್ಷ ಪಾವತಿಸಿದ ಇಂಟರ್ನ್‌ಶಿಪ್).
• ಶುಲ್ಕ: ಸರ್ಕಾರಿ ಕೋಟಾ ₹85,000 - ₹1.5 ಲಕ್ಷ/ವರ್ಷ; ಖಾಸಗಿ ಕೋಟಾ ₹3.5 - ₹6.5 ಲಕ್ಷ/ವರ್ಷ.
• ವೃತ್ತಿ ಅವಕಾಶಗಳು: ದಂತ ಶಸ್ತ್ರಚಿಕಿತ್ಸಕ (Dental Surgeon), ಕಾಸ್ಮೆಟಿಕ್ ಡೆಂಟಿಸ್ಟ್ರಿ, ಸೇನೆಯಲ್ಲಿ ಡೆಂಟಲ್ ಕಾರ್ಪ್ಸ್, MDS ಸ್ನಾತಕೋತ್ತರ. ಆರಂಭಿಕ ವೇತನ: ₹6 LPA - ₹14 LPA.
• ಉನ್ನತ ಕಾಲೇಜುಗಳು: ಮಣಿಪಾಲ ಡೆಂಟಲ್ ಕಾಲೇಜು, AB ಶೆಟ್ಟಿ ಇನ್‌ಸ್ಟಿಟ್ಯೂಟ್ ಮಂಗಳೂರು, ಸರ್ಕಾರಿ ಡೆಂಟಲ್ ಕಾಲೇಜ್ ಬೆಂಗಳೂರು.`,
        sources: [
          { title: "KEA Karnataka Portal", url: "https://cetonline.karnataka.gov.in/kea/" },
          { title: "MCC Medical Counselling", url: "https://mcc.nic.in" }
        ]
      };
    }

    if (q.includes('mbbs') || q.includes('medical') || q.includes('doctor') || q.includes('ವೈದ್ಯ')) {
      return {
        replyText: `📌 MBBS ಪ್ರವೇಶ ಮತ್ತು ವೃತ್ತಿ ಮಾರ್ಗದರ್ಶನ:

• ಅರ್ಹತೆ: 12ನೇ ತರಗತಿ PCB ಕನಿಷ್ಠ 50% ಮತ್ತು NEET-UG ಅರ್ಹತೆ.
• ಕೌನ್ಸೆಲಿಂಗ್ ಪ್ರಾಧಿಕಾರಗಳು: MCC (15% AIQ & ಡೀಮ್ಡ್) ಮತ್ತು KEA (85% ರಾಜ್ಯ ಕೋಟಾ).
• ಅವಧಿ: 5.5 ವರ್ಷಗಳು (4.5 ವರ್ಷ ಅಕಾಡೆಮಿಕ್ + 1 ವರ್ಷ ರೋಟೇಟರಿ ಇಂಟರ್ನ್‌ಶಿಪ್).
• ಕಟ್‌ಆಫ್ ಶ್ರೇಣಿ: ಸರ್ಕಾರಿ ಕಾಲೇಜುಗಳಿಗೆ NEET ನಲ್ಲಿ 610+ ಅಂಕಗಳು (ಜನರಲ್ ಕೆಟಗರಿ).
• ಶುಲ್ಕ: ಸರ್ಕಾರಿ ಮೆಡಿಕಲ್ ಕಾಲೇಜುಗಳಲ್ಲಿ ₹15,000 - ₹60,000/ವರ್ಷ.
• ಪ್ರಮುಖ ಸಂಸ್ಥೆಗಳು: AIIMS, BMCRI ಬೆಂಗಳೂರು, MMCRI ಮೈಸೂರು, KMC ಮಣಿಪಾಲ.`,
        sources: [
          { title: "NTA NEET Portal", url: "https://neet.nta.nic.in" },
          { title: "KEA Karnataka", url: "https://kea.kar.nic.in" }
        ]
      };
    }

    if (q.includes('engineering') || q.includes('btech') || q.includes('cse') || q.includes('ಇಂಜಿನಿಯರಿಂಗ್')) {
      return {
        replyText: `📌 B.Tech / ಇಂಜಿನಿಯರಿಂಗ್ ಸಮಗ್ರ ಮಾಹಿತಿ:

• ಪ್ರವೇಶ ಪರೀಕ್ಷೆಗಳು: KCET (ಕರ್ನಾಟಕ ವಿದ್ಯಾರ್ಥಿಗಳಿಗೆ), JEE Main (NIT/IIIT ಗಾಗಿ), COMEDK.
• ಅಧಿಕೃತ ಕೌನ್ಸೆಲಿಂಗ್: JoSAA (josaa.nic.in) ಮತ್ತು KEA (kea.kar.nic.in).
• ಪ್ರಮುಖ ಬ್ರಾಂಚ್‌ಗಳು: Computer Science & Engg, AI & Machine Learning, Data Science, ECE.
• ಶುಲ್ಕ: KEA ಸರ್ಕಾರಿ ಸೀಟು ₹96,500/ವರ್ಷ; COMEDK ಸೀಟು ₹2.4 - ₹2.8 ಲಕ್ಷ/ವರ್ಷ.
• ಟಾಪ್ ಕಾಲೇಜುಗಳು: NIT ಸೂರತ್ಕಲ್, RVCE ಬೆಂಗಳೂರು, BMSCE, MSRIT, PES ವಿಶ್ವವಿದ್ಯಾಲಯ.
• ಸರಾಸರಿ ಪ್ಯಾಕೇಜ್: ₹8 LPA - ₹35 LPA.`,
        sources: [
          { title: "JoSAA Official Portal", url: "https://josaa.nic.in" },
          { title: "KEA Official Website", url: "https://kea.kar.nic.in" }
        ]
      };
    }

    return {
      replyText: `ನಮಸ್ಕಾರ ${studentName}! ನಿಮ್ಮ ಪ್ರಶ್ನೆಗೆ ಇಲ್ಲಿದೆ ಸ್ಪಷ್ಟ ಮಾಹಿತಿ:

• ನಿಮ್ಮ ಶೈಕ್ಷಣಿಕ ಹಿನ್ನೆಲೆ (${studentStream}) ಆಧಾರದ ಮೇಲೆ ಸೂಕ್ತ ಕೋರ್ಸ್‌ಗಳು, ಪ್ರವೇಶ ಪರೀಕ್ಷೆಗಳು ಮತ್ತು ಕೌನ್ಸೆಲಿಂಗ್ ಪ್ರಕ್ರಿಯೆಯನ್ನು ಯೋಜಿಸುವುದು ಮುಖ್ಯ.
• ಪ್ರವೇಶ ಪರೀಕ್ಷೆಗಳು: ನಿಮ್ಮ ಗುರಿಗೆ ತಕ್ಕಂತೆ NTA, KEA, JoSAA, NEET, JEE ಅಥವಾ CUET-UG ಅಧಿಕೃತ ಪೋರ್ಟಲ್‌ಗಳನ್ನು ಪರಿಶೀಲಿಸಿ.
• ವಿದ್ಯಾರ್ಥಿವೇತನಗಳು: ಕರ್ನಾಟಕದ ವಿದ್ಯಾರ್ಥಿಗಳು SSP ಪೋರ್ಟಲ್ (ssp.postmatric.karnataka.gov.in) ಮೂಲಕ ಶುಲ್ಕ ವಿನಾಯಿತಿ ಮತ್ತು ವಿದ್ಯಾರ್ಥಿವೇತನಕ್ಕೆ ಅರ್ಜಿ ಸಲ್ಲಿಸಬಹುದು.

ಹೆಚ್ಚಿನ ವಿವರಗಳಿಗಾಗಿ ನಿರ್ದಿಷ್ಟ ಕೋರ್ಸ್, ಕಾಲೇಜು ಅಥವಾ ಪ್ರವೇಶ ಪರೀಕ್ಷೆಯ ಬಗ್ಗೆ ಕೇಳಿ!`,
      sources: [
        { title: "KEA Karnataka", url: "https://kea.kar.nic.in" },
        { title: "SSP Scholarship Portal", url: "https://ssp.postmatric.karnataka.gov.in" }
      ]
    };
  }

  // English fallback responses
  if (q.includes('scientist') || q.includes('research') || q.includes('isro') || q.includes('drdo') || q.includes('barc')) {
    return {
      replyText: `🚀 Comprehensive Pathway to Becoming a Scientist in India:

• 1. Educational Foundation (12th Science):
  Ensure 60%+ in Physics, Chemistry, and Mathematics/Biology. Having strong analytical and problem-solving skills is essential.

• 2. Premier Indian Research Institutes:
  • IISc Bangalore (Ranked #1 in India for science & research)
  • IISERs (7 campuses: Pune, Mohali, Kolkata, Bhopal, TVM, Tirupati, Berhampur)
  • NISER Bhubaneswar & UM-DAE CEBS Mumbai (Autonomous under Dept of Atomic Energy)
  • IITs & Central Universities

• 3. Key Entrance Examinations:
  • IAT (IISER Aptitude Test): For admission to 5-year Dual BS-MS degree at IISERs.
  • NEST (National Entrance Screening Test): For 5-year Integrated M.Sc at NISER & CEBS.
  • JEE Advanced: For admission to 4-year BS in Research at IISc Bangalore.
  • CUET-UG: For top Central University B.Sc (Hons) programs.

• 4. The Degree Progression:
  10+2 ➔ 4-Year BS or 5-Year Integrated BS-MS ➔ Master's (M.Sc/M.Tech) ➔ Ph.D. (Doctorate with peer-reviewed research).

• 5. Top Research Organizations & Scientist Roles:
  • ISRO: Join via the ISRO Centralised Recruitment Board (ICRB) exam or direct campus hiring from IIST/IISc.
  • DRDO: Join as Scientist 'B' through GATE scores or RAC written exams.
  • BARC & DAE: Nuclear Scientist entry through OCES/DGFS programs.
  • CSIR Labs: Senior Research Fellow (SRF) & Scientist grade posts.

• 6. Fellowships & Scholarships:
  • INSPIRE SHE Scholarship (DST): ₹80,000/year throughout Bachelor's and Master's.
  • CSIR-UGC NET JRF: ₹37,000/month + HRA fellowship during Ph.D.

💡 Student Tip: Target the IISER Aptitude Test (IAT) and NEST right after 12th standard for direct entry into premier research pipelines!`,
      sources: [
        { title: "IISER Admissions Portal (IAT)", url: "https://iiseradmission.in" },
        { title: "National Entrance Screening Test (NEST)", url: "https://www.nestexam.in" },
        { title: "IISc Bangalore UG Programs", url: "https://admissions.iisc.ac.in" },
        { title: "ISRO Careers (ICRB)", url: "https://www.isro.gov.in/Careers.html" }
      ]
    };
  }

  if (q.includes('bds') || q.includes('dental')) {
    return {
      replyText: `📌 BDS (Bachelor of Dental Surgery) Admission & Career Guide:

• Eligibility: 12th PCB with minimum 50% + valid score in NEET-UG (conducted by NTA).
• Official Authorities: KEA (kea.kar.nic.in) for Karnataka 85% state quota; MCC (mcc.nic.in) for 15% All India Quota & Deemed Universities.
• Duration: 5 Years (4 years academics + 1 year compulsory paid rotatory internship).
• Expected Fees:
  - Government Dental Colleges: ₹40,000 - ₹85,000 / year.
  - Private Colleges (Govt Quota): ₹1.2 Lakhs - ₹2.5 Lakhs / year.
  - Management / NRI Quota: ₹4 Lakhs - ₹8 Lakhs / year.
• Career Scope & Salaries: Dental Surgeon, Endodontist, Cosmetic Dentist, Army Dental Corps. Starting packages range from ₹6 LPA to ₹15 LPA.
• Top Institutes: Manipal College of Dental Sciences, Government Dental College Bangalore, Maulana Azad Dental Sciences (New Delhi).`,
      sources: [
        { title: "KEA Karnataka Admissions", url: "https://kea.kar.nic.in" },
        { title: "MCC Counselling Portal", url: "https://mcc.nic.in" },
        { title: "Dental Council of India", url: "https://dciindia.gov.in" }
      ]
    };
  }

  if (q.includes('mbbs') || q.includes('doctor') || q.includes('medical')) {
    return {
      replyText: `📌 MBBS Admission & Career Roadmap:

• Eligibility: 10+2 with Physics, Chemistry, Biology and English with 50%+ marks; mandatory qualification in NEET-UG.
• Official Counselling Bodies: MCC (mcc.nic.in) for 15% AIQ, Central Institutes & AIIMS; KEA (kea.kar.nic.in) for Karnataka State Quota.
• Duration: 5.5 Years (4.5 years coursework + 1 year paid internship).
• Government College Cutoffs: Typically 615+ marks in NEET-UG for General category.
• Top Medical Institutions: AIIMS New Delhi, BMCRI Bangalore, MMCRI Mysore, CMC Vellore, KMC Manipal.`,
      sources: [
        { title: "NTA NEET-UG Portal", url: "https://neet.nta.nic.in" },
        { title: "Medical Counselling Committee (MCC)", url: "https://mcc.nic.in" }
      ]
    };
  }

  if (q.includes('engineering') || q.includes('btech') || q.includes('cse') || q.includes('pcm')) {
    return {
      replyText: `📌 B.Tech / Engineering Pathways & Admission Details:

• Key Entrance Exams: KCET (Karnataka domicile), JEE Main & JEE Advanced (IITs/NITs/IIITs), COMEDK UGET.
• Counselling Authorities: JoSAA (josaa.nic.in) for IITs/NITs; KEA (kea.kar.nic.in) for Karnataka Engineering colleges.
• High-Demand Specializations: Computer Science & Engineering, AI & Machine Learning, Data Science, Electronics & Communication.
• Tuition Fees: Government KCET quota ~₹96,000/year; COMEDK ~₹2.4 - ₹2.8 Lakhs/year.
• Top Engineering Institutes: NIT Surathkal, RV College of Engineering (RVCE), BMSCE Bangalore, MSRIT, PES University.`,
      sources: [
        { title: "JoSAA Official Portal", url: "https://josaa.nic.in" },
        { title: "KEA Engineering Admissions", url: "https://kea.kar.nic.in" }
      ]
    };
  }

  if (q.includes('scholarship') || q.includes('fee waiver') || q.includes('financial aid')) {
    return {
      replyText: `📌 Government & Private Scholarships for Students:

• SSP Karnataka (State Scholarship Portal - ssp.postmatric.karnataka.gov.in): Post-matric scholarships, e-Pass fee reimbursement, and Vidya Siri for SC/ST/OBC/Minority students.
• National Scholarship Portal (NSP - scholarships.gov.in): Central sector schemes for college and university students scoring 80%+ in 12th board exams.
• Reliance Foundation Undergraduate Scholarship: Up to ₹2,00,000 for degree studies across all streams.
• INSPIRE SHE: ₹80,000/year for natural and basic science students in top 1% of 12th board.`,
      sources: [
        { title: "Karnataka SSP Portal", url: "https://ssp.postmatric.karnataka.gov.in" },
        { title: "National Scholarship Portal", url: "https://scholarships.gov.in" }
      ]
    };
  }

  return {
    replyText: `Hello ${studentName}! Here is guidance tailored to your query:

• For ${query.trim()}:
• Explore established degree pathways corresponding to your ${studentStream} qualification.
• Key Entrance Exams & Portals: Always verify notifications on official platforms like NTA (nta.ac.in), KEA (kea.kar.nic.in), and JoSAA (josaa.nic.in).
• Scholarships: Check the Karnataka SSP portal (ssp.postmatric.karnataka.gov.in) and National Scholarship Portal for financial support.
• Need 1-on-1 Guidance? You can book an appointment with our senior counsellors using coupon code 'FIRSTFREE' in the app!

Ask me any specific question about cutoffs, eligibility, entrance dates, or college comparisons!`,
    sources: [
      { title: "KEA Karnataka Official", url: "https://kea.kar.nic.in" },
      { title: "National Testing Agency", url: "https://nta.ac.in" }
    ]
  };
}

const server = http.createServer(async (req, res) => {
  // CORS Headers
  res.setHeader('Access-Control-Allow-Origin', '*');
  res.setHeader('Access-Control-Allow-Methods', 'GET, POST, OPTIONS');
  res.setHeader('Access-Control-Allow-Headers', 'Content-Type, Authorization, x-goog-api-key');

  if (req.method === 'OPTIONS') {
    res.writeHead(204);
    res.end();
    return;
  }

  if (req.url === '/health' || req.url === '/api/health') {
    res.writeHead(200, { 'Content-Type': 'application/json' });
    res.end(JSON.stringify({ status: 'ok', service: 'Gemini Student Counsellor Backend' }));
    return;
  }

  if (req.url === '/api/chat' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => {
      body += chunk;
    });

    req.on('end', async () => {
      try {
        const payload = JSON.parse(body || '{}');
        const userMessage = payload.userMessage || payload.message || '';
        const studentName = payload.studentName || 'Student';
        const studentStream = payload.studentStream || '12th Standard';
        const isKannada = Boolean(
          payload.isKannada ||
          /[\u0C80-\u0CFF]/.test(userMessage) ||
          userMessage.toLowerCase().includes('kannada') ||
          userMessage.includes('ಕನ್ನಡ')
        );
        const history = payload.history || [];

        const apiKey = getApiKey();

        if (apiKey) {
          try {
            const geminiResult = await callGemini(apiKey, userMessage, studentName, studentStream, isKannada, history);
            if (geminiResult && geminiResult.replyText) {
              res.writeHead(200, { 'Content-Type': 'application/json' });
              res.end(JSON.stringify({
                replyText: geminiResult.replyText,
                sources: geminiResult.sources || []
              }));
              return;
            }
          } catch (e) {
            console.error('Gemini call failed:', e);
          }
        }

        // If Gemini is unreachable or key is missing, return clean status without hardcoded fake responses
        const errorMsg = isKannada
          ? `ಕ್ಷಮಿಸಿ ${studentName}, AI ಸರ್ವರ್ ಸಂಪರ್ಕದಲ್ಲಿ ತೊಂದರೆ ಉಂಟಾಗಿದೆ. ದಯವಿಟ್ಟು ಮತ್ತೊಮ್ಮೆ ಕೇಳಿ.`
          : `I am having trouble reaching the Gemini AI service right now, ${studentName}. Please check your connection and try asking again.`;
        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ replyText: errorMsg, sources: [] }));
      } catch (err) {
        console.error('Error handling /api/chat:', err);
        res.writeHead(500, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ error: 'Internal server error', details: err.message }));
      }
    });
    return;
  }

  if (req.url === '/api/recommendations' && req.method === 'POST') {
    let body = '';
    req.on('data', chunk => {
      body += chunk;
    });

    req.on('end', async () => {
      try {
        const payload = JSON.parse(body || '{}');
        const qualification = payload.qualification || '12th Standard';
        const percentage = Number(payload.percentage) || 75;
        const state = payload.state || 'Karnataka';
        const budgetLakhs = Number(payload.budgetLakhs) || 2.5;
        const preferredCourse = payload.preferredCourse || 'Engineering';
        const preferredCity = payload.preferredCity || 'Bangalore';
        const additionalGoals = payload.additionalGoals || '';

        const apiKey = getApiKey();
        if (apiKey) {
          try {
            const recPrompt = `You are an expert Indian Student Career & College Counsellor for CareerPath.
Analyze this student profile:
- Current Qualification: ${qualification}
- Marks / Percentage: ${percentage}%
- Home State: ${state}
- Annual Budget: ₹${budgetLakhs} Lakhs/year
- Preferred Course: ${preferredCourse}
- Preferred City: ${preferredCity}
- Additional Goals: ${additionalGoals}

Ground your recommendations using official cutoff trends, fee structures, and NIRF rankings.
Do NOT use markdown symbols like asterisks (** or *), hashtags (#), or backticks in any string value.

Return ONLY a valid JSON object without markdown fences, formatted exactly as:
{
  "matchScore": 92,
  "recommendedCollegeName": "College Name",
  "recommendedCourse": "Course Name",
  "reasoning": "3-4 detailed sentences explaining why this fits.",
  "eligibilityCheck": "Detailed cutoff and eligibility match status from official authority (KEA/NTA/JoSAA/State CET)",
  "estimatedFeeRange": "₹X - ₹Y Lakhs",
  "careerOpportunities": "Key roles and expected packages",
  "alternateColleges": ["Alternate College 1", "Alternate College 2", "Alternate College 3"]
}`;

            for (const model of ['gemini-3.5-flash', 'gemini-3.1-flash-lite-preview']) {
              try {
                const url = `https://generativelanguage.googleapis.com/v1beta/models/${model}:generateContent?key=${apiKey}`;
                const res = await fetch(url, {
                  method: 'POST',
                  headers: { 'Content-Type': 'application/json' },
                  body: JSON.stringify({
                    contents: [{ role: 'user', parts: [{ text: recPrompt }] }],
                    generationConfig: { temperature: 0.5 }
                  }),
                  signal: AbortSignal.timeout(6000)
                });
                if (res.ok) {
                  const data = await res.json();
                  const rawText = data.candidates?.[0]?.content?.parts?.[0]?.text;
                  if (rawText) {
                    const cleanJson = rawText.replace(/```json/g, '').replace(/```/g, '').trim();
                    const parsed = JSON.parse(cleanJson);
                    res.writeHead(200, { 'Content-Type': 'application/json' });
                    res.end(JSON.stringify(parsed));
                    return;
                  }
                }
              } catch (modelErr) {}
            }
          } catch (recErr) {
            console.warn('Gemini recommendations call failed:', recErr.message);
          }
        }

        // Fallback intelligent recommendation
        const isHighMarks = percentage >= 80;
        const isMedical = /mbbs|bams|medical|bds/i.test(preferredCourse);
        const isEngg = /engineering|tech|bca/i.test(preferredCourse);

        const colName = isMedical && isHighMarks ? "All India Institute of Medical Sciences (AIIMS) New Delhi"
          : isMedical ? "Christian Medical College (CMC) Vellore"
          : isEngg && isHighMarks ? "Indian Institute of Technology (IIT) Bombay"
          : isEngg ? "National Institute of Technology (NIT) Karnataka, Surathkal"
          : /mba/i.test(preferredCourse) ? "Indian Institute of Management (IIM) Ahmedabad"
          : /law/i.test(preferredCourse) ? "National Law School of India University (NLSIU) Bangalore"
          : "IIT Madras";

        const score = percentage >= 90 ? 96 : percentage >= 80 ? 91 : percentage >= 70 ? 85 : 78;

        const result = {
          matchScore: score,
          recommendedCollegeName: colName,
          recommendedCourse: preferredCourse,
          reasoning: `Based on your ${percentage}% in ${qualification} and an annual budget of ₹${budgetLakhs} Lakhs, ${colName} offers ideal academic faculty, accreditation, and placement track record in ${preferredCity}.`,
          eligibilityCheck: isHighMarks ? "Eligible for direct merit rounds & scholarship eligibility." : "Eligible through state quota & university entrance test.",
          estimatedFeeRange: `₹${(budgetLakhs * 0.7).toFixed(1)} - ₹${budgetLakhs} Lakhs / Year`,
          careerOpportunities: "Average campus placements ranging from ₹10 - ₹24 LPA with top Tier-1 industry recruiters.",
          alternateColleges: ["IIT Delhi", "IIT Madras", "BITS Pilani"]
        };

        res.writeHead(200, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify(result));
      } catch (err) {
        console.error('Error handling /api/recommendations:', err);
        res.writeHead(500, { 'Content-Type': 'application/json' });
        res.end(JSON.stringify({ error: 'Internal server error', details: err.message }));
      }
    });
    return;
  }

  // Default route
  res.writeHead(200, { 'Content-Type': 'application/json' });
  res.end(JSON.stringify({
    name: "CareerPath Gemini Counsellor Server",
    status: "running",
    endpoints: ["POST /api/chat", "POST /api/recommendations", "GET /health"]
  }));
});

server.listen(PORT, '0.0.0.0', () => {
  console.log(`Gemini Counsellor Backend running on http://0.0.0.0:${PORT}`);
});
