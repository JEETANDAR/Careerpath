package com.aistudio.carrerpath.counseling.data.repository

import com.aistudio.carrerpath.counseling.data.model.*

object PsychometricTestEngine {

    val scaleOptions = listOf(
        PsychometricOption("Dislike", 0),
        PsychometricOption("Slightly Dislike", 1),
        PsychometricOption("Neither like nor dislike", 2),
        PsychometricOption("Slightly Enjoy", 3),
        PsychometricOption("Enjoy", 4)
    )

    val scaleOptionsKn = listOf(
        PsychometricOption("ಇಷ್ಟವಿಲ್ಲ (Dislike)", 0),
        PsychometricOption("ಸ್ವಲ್ಪ ಇಷ್ಟವಿಲ್ಲ (Slightly Dislike)", 1),
        PsychometricOption("ಇಷ್ಟವೂ ಇಲ್ಲ ಕಷ್ಟವೂ ಇಲ್ಲ (Neutral)", 2),
        PsychometricOption("ಸ್ವಲ್ಪ ಇಷ್ಟ (Slightly Enjoy)", 3),
        PsychometricOption("ತುಂಬಾ ಇಷ್ಟ (Enjoy)", 4)
    )

    val questions: List<PsychometricQuestion> = listOf(
        // Realistic (R1 - R5)
        PsychometricQuestion(
            id = 1,
            code = "R1",
            category = "Realistic",
            activity = "Creating tangible things using my hands and body",
            activityKn = "ನನ್ನ ಕೈಗಳು ಮತ್ತು ದೇಹವನ್ನು ಬಳಸಿ ಸ್ಪಷ್ಟವಾದ ವಸ್ತುಗಳನ್ನು ನಿರ್ಮಿಸುವುದು"
        ),
        PsychometricQuestion(
            id = 2,
            code = "R2",
            category = "Realistic",
            activity = "Working with gadgets",
            activityKn = "ಗ್ಯಾಜೆಟ್‌ಗಳು ಮತ್ತು ಎಲೆಕ್ಟ್ರಾನಿಕ್ ಯಂತ್ರಗಳೊಂದಿಗೆ ಕೆಲಸ ಮಾಡುವುದು"
        ),
        PsychometricQuestion(
            id = 3,
            code = "R3",
            category = "Realistic",
            activity = "Relentlessly working towards my goal",
            activityKn = "ನನ್ನ ಗುರಿಯ ಕಡೆಗೆ ದೃಢವಾಗಿ ಮತ್ತು ನಿರಂತರವಾಗಿ ಕೆಲಸ ಮಾಡುವುದು"
        ),
        PsychometricQuestion(
            id = 4,
            code = "R4",
            category = "Realistic",
            activity = "Working with plants, animals, mud, water, wood, metal, etc. in the outdoors",
            activityKn = "ಹೊರಾಂಗಣದಲ್ಲಿ ಸಸ್ಯಗಳು, ಪ್ರಾಣಿಗಳು, ನೀರು, ಮರ, ಲೋಹ ಇತ್ಯಾದಿಗಳೊಂದಿಗೆ ಕೆಲಸ ಮಾಡುವುದು"
        ),
        PsychometricQuestion(
            id = 5,
            code = "R5",
            category = "Realistic",
            activity = "Being systematic – following step-by-step procedure",
            activityKn = "ವ್ಯವಸ್ಥಿತವಾಗಿರುವುದು – ಹಂತ-ಹಂತದ ವಿಧಾನವನ್ನು ಪಾಲಿಸುವುದು"
        ),

        // Investigative (I1 - I5)
        PsychometricQuestion(
            id = 6,
            code = "I1",
            category = "Investigative",
            activity = "Being suspicious (doubting what appears)",
            activityKn = "ಸಂಶಯಾತ್ಮಕವಾಗಿರುವುದು (ಕಂಡದ್ದನ್ನು ಸೂಕ್ಷ್ಮವಾಗಿ ಪ್ರಶ್ನಿಸುವುದು ಮತ್ತು ಪರಿಶೀಲಿಸುವುದು)"
        ),
        PsychometricQuestion(
            id = 7,
            code = "I2",
            category = "Investigative",
            activity = "Analysing a situation or thing for its pros and cons",
            activityKn = "ಒಂದು ಸನ್ನಿವೇಶ ಅಥವಾ ವಸ್ತುವಿನ ಒಳಿತು-ಕೆಡುಕುಗಳು ಮತ್ತು ಸಾಧಕ-ಬಾಧಕಗಳನ್ನು ವಿಶ್ಲೇಷಿಸುವುದು"
        ),
        PsychometricQuestion(
            id = 8,
            code = "I3",
            category = "Investigative",
            activity = "Making rational decisions",
            activityKn = "ತಾರ್ಕಿಕ ಮತ್ತು ವಿವೇಚನಾಯುಕ್ತ ನಿರ್ಧಾರಗಳನ್ನು ತೆಗೆದುಕೊಳ್ಳುವುದು"
        ),
        PsychometricQuestion(
            id = 9,
            code = "I4",
            category = "Investigative",
            activity = "Being curious",
            activityKn = "ಕುತೂಹಲದಿಂದ ಇರುವುದು ಮತ್ತು ಹೊಸ ವಿಷಯಗಳನ್ನು ಕಲಿಯುವುದು"
        ),
        PsychometricQuestion(
            id = 10,
            code = "I5",
            category = "Investigative",
            activity = "Noticing things and events around me",
            activityKn = "ನನ್ನ ಸುತ್ತಮುತ್ತಲಿನ ಸೂಕ್ಷ್ಮ ಬದಲಾವಣೆಗಳು ಮತ್ತು ಘಟನೆಗಳನ್ನು ಗಮನಿಸುವುದು"
        ),

        // Artistic (A1 - A5)
        PsychometricQuestion(
            id = 11,
            code = "A1",
            category = "Artistic",
            activity = "Using my imagination",
            activityKn = "ನನ್ನ ಕಲ್ಪನಾಶಕ್ತಿಯನ್ನು ಸೃಜನಾತ್ಮಕವಾಗಿ ಬಳಸುವುದು"
        ),
        PsychometricQuestion(
            id = 12,
            code = "A2",
            category = "Artistic",
            activity = "Thinking outside the box",
            activityKn = "ವಿಭಿನ್ನವಾಗಿ ಮತ್ತು ಸೃಜನಶೀಲವಾಗಿ ಯೋಚಿಸುವುದು (Thinking outside the box)"
        ),
        PsychometricQuestion(
            id = 13,
            code = "A3",
            category = "Artistic",
            activity = "Being emotional",
            activityKn = "ಭಾವನಾತ್ಮಕವಾಗಿ ಮತ್ತು ಆಳವಾಗಿ ಸ್ಪಂದಿಸುವುದು"
        ),
        PsychometricQuestion(
            id = 14,
            code = "A4",
            category = "Artistic",
            activity = "Being expressive",
            activityKn = "ನನ್ನ ಭಾವನೆ ಮತ್ತು ಕಲ್ಪನೆಗಳನ್ನು ಮುಕ್ತವಾಗಿ ಅಭಿವ್ಯಕ್ತಪಡಿಸುವುದು"
        ),
        PsychometricQuestion(
            id = 15,
            code = "A5",
            category = "Artistic",
            activity = "Being brave and daring",
            activityKn = "ಧೈರ್ಯಶಾಲಿ ಮತ್ತು ಸಾಹಸಮಯವಾಗಿ ಹೊಸ ಪ್ರಯೋಗಗಳನ್ನು ಮಾಡುವುದು"
        ),

        // Social (S1 - S5)
        PsychometricQuestion(
            id = 16,
            code = "S1",
            category = "Social",
            activity = "Being kind to others and helping people in need like poor, disabled, old, sick, underserved, etc.",
            activityKn = "ಇತರರಿಗೆ ದಯೆ ತೋರಿಸುವುದು ಮತ್ತು ಬಡವರು, ವೃದ್ಧರು, ರೋಗಿಗಳು ಹಾಗೂ ಅಗತ್ಯವಿರುವವರಿಗೆ ಸಹಾಯ ಮಾಡುವುದು"
        ),
        PsychometricQuestion(
            id = 17,
            code = "S2",
            category = "Social",
            activity = "Waiting for someone or something",
            activityKn = "ಸಹನೆಯಿಂದ ಕಾಯುವುದು ಮತ್ತು ಇತರರಿಗೆ ಸಮಯ ನೀಡುವುದು"
        ),
        PsychometricQuestion(
            id = 18,
            code = "S3",
            category = "Social",
            activity = "Relating to other’s feelings and problems like my own",
            activityKn = "ಇತರರ ಕಷ್ಟ-ಸುಖಗಳನ್ನು ನನ್ನದೇ ಎಂದು ಅರ್ಥಮಾಡಿಕೊಳ್ಳುವುದು ಮತ್ತು ಸಹಾನುಭೂತಿ ಹೊಂದುವುದು"
        ),
        PsychometricQuestion(
            id = 19,
            code = "S4",
            category = "Social",
            activity = "Working in harmony with others as a team with dependency",
            activityKn = "ತಂಡವಾಗಿ ಇತರರೊಂದಿಗೆ ಸಾಮರಸ್ಯದಿಂದ ಮತ್ತು ಸಹಕಾರದಿಂದ ಕೆಲಸ ಮಾಡುವುದು"
        ),
        PsychometricQuestion(
            id = 20,
            code = "S5",
            category = "Social",
            activity = "Forgiving people who cause me harm",
            activityKn = "ತಪ್ಪು ಮಾಡಿದವರನ್ನು ಕ್ಷಮಿಸುವುದು ಮತ್ತು ಸದ್ಭಾವನೆಯಿಂದ ವರ್ತಿಸುವುದು"
        ),

        // Enterprising (E1 - E5)
        PsychometricQuestion(
            id = 21,
            code = "E1",
            category = "Enterprising",
            activity = "Socializing with others",
            activityKn = "ಇತರರೊಂದಿಗೆ ಸುಲಭವಾಗಿ ಬೆರೆಯುವುದು ಮತ್ತು ಹೊಸ ಪರಿಚಯಗಳನ್ನು ಮಾಡಿಕೊಳ್ಳುವುದು"
        ),
        PsychometricQuestion(
            id = 22,
            code = "E2",
            category = "Enterprising",
            activity = "Taking risks",
            activityKn = "ಲೆಕ್ಕಾಚಾರದ ಅಪಾಯಗಳು ಮತ್ತು ಸವಾಲುಗಳನ್ನು ಎದುರಿಸುವುದು"
        ),
        PsychometricQuestion(
            id = 23,
            code = "E3",
            category = "Enterprising",
            activity = "Being enthusiastic",
            activityKn = "ಉತ್ಸಾಹಭರಿತ ಮತ್ತು ಚೈತನ್ಯದಾಯಕವಾಗಿ ಕೆಲಸಗಳನ್ನು ಆರಂಭಿಸುವುದು"
        ),
        PsychometricQuestion(
            id = 24,
            code = "E4",
            category = "Enterprising",
            activity = "Imagining everything will go in my favour",
            activityKn = "ಧನಾತ್ಮಕ ಮನೋಭಾವದಿಂದ ಎಲ್ಲವೂ ನನ್ನ ಪರವಾಗಿ ಉತ್ತಮವಾಗಿ ನಡೆಯುತ್ತದೆ ಎಂದು ನಂಬುವುದು"
        ),
        PsychometricQuestion(
            id = 25,
            code = "E5",
            category = "Enterprising",
            activity = "Convincing people",
            activityKn = "ಜನರನ್ನು ಮನವೊಲಿಸುವುದು, ಮುನ್ನಡೆಸುವುದು ಮತ್ತು ಪ್ರೇರೇಪಿಸುವುದು"
        ),

        // Conventional (C1 - C5)
        PsychometricQuestion(
            id = 26,
            code = "C1",
            category = "Conventional",
            activity = "Working with mind, precise calculations",
            activityKn = "ನಿಖರವಾದ ಲೆಕ್ಕಾಚಾರಗಳು ಮತ್ತು ಮನಸ್ಸಿನೊಂದಿಗೆ ಸೂಕ್ಷ್ಮವಾಗಿ ಕೆಲಸ ಮಾಡುವುದು"
        ),
        PsychometricQuestion(
            id = 27,
            code = "C2",
            category = "Conventional",
            activity = "Being precise, methodical and detail oriented",
            activityKn = "ನಿಖರ, ಕ್ರಮಬದ್ಧ ಮತ್ತು ಸೂಕ್ಷ್ಮ ವಿವರ-ಆಧಾರಿತವಾಗಿರುವುದು"
        ),
        PsychometricQuestion(
            id = 28,
            code = "C3",
            category = "Conventional",
            activity = "Following rules",
            activityKn = "ನಿಯಮಗಳು, ಪದ್ಧತಿಗಳು ಮತ್ತು ನಿಬಂಧನೆಗಳನ್ನು ಶ್ರದ್ಧೆಯಿಂದ ಪಾಲಿಸುವುದು"
        ),
        PsychometricQuestion(
            id = 29,
            code = "C4",
            category = "Conventional",
            activity = "Working hard and sincerely",
            activityKn = "ಕಠಿಣ ಪರಿಶ್ರಮ ಮತ್ತು ಪ್ರಾಮಾಣಿಕತೆಯಿಂದ ಕಾರ್ಯ ನಿರ್ವಹಿಸುವುದು"
        ),
        PsychometricQuestion(
            id = 30,
            code = "C5",
            category = "Conventional",
            activity = "Being courteous to others",
            activityKn = "ಇತರರೊಂದಿಗೆ ಸಭ್ಯ, ವಿನಮ್ರ ಮತ್ತು ಗೌರವಯುತವಾಗಿ ವರ್ತಿಸುವುದು"
        )
    )

    /**
     * Evaluates RIASEC assessment using the exact scoring matrix from the PDF:
     * - Realistic: R1 to R5 (Max 20 pts)
     * - Investigative: I1 to I5 (Max 20 pts)
     * - Artistic: A1 to A5 (Max 20 pts)
     * - Social: S1 to S5 (Max 20 pts)
     * - Enterprising: E1 to E5 (Max 20 pts)
     * - Conventional: C1 to C5 (Max 20 pts)
     *
     * Total Test Score = Sum of all 30 questions (Max 120 pts)
     * Each category % = (Category Points / 20) * 100
     * Category RIASEC Index = (Category Total / 120) * 100
     */
    fun evaluateRiasecScores(answers: Map<Int, Int>): PsychometricAssessmentResult {
        var rTotal = 0
        var iTotal = 0
        var aTotal = 0
        var sTotal = 0
        var eTotal = 0
        var cTotal = 0

        questions.forEach { q ->
            val score = answers[q.id] ?: 2 // Default neutral if unanswered
            when (q.category) {
                "Realistic" -> rTotal += score
                "Investigative" -> iTotal += score
                "Artistic" -> aTotal += score
                "Social" -> sTotal += score
                "Enterprising" -> eTotal += score
                "Conventional" -> cTotal += score
            }
        }

        val totalPoints = rTotal + iTotal + aTotal + sTotal + eTotal + cTotal

        // Category percentage out of 20 points
        val rPct = ((rTotal / 20.0) * 100).toInt().coerceIn(0, 100)
        val iPct = ((iTotal / 20.0) * 100).toInt().coerceIn(0, 100)
        val aPct = ((aTotal / 20.0) * 100).toInt().coerceIn(0, 100)
        val sPct = ((sTotal / 20.0) * 100).toInt().coerceIn(0, 100)
        val ePct = ((eTotal / 20.0) * 100).toInt().coerceIn(0, 100)
        val cPct = ((cTotal / 20.0) * 100).toInt().coerceIn(0, 100)

        val dimensionScores = linkedMapOf(
            "Realistic (R)" to rPct,
            "Investigative (I)" to iPct,
            "Artistic (A)" to aPct,
            "Social (S)" to sPct,
            "Enterprising (E)" to ePct,
            "Conventional (C)" to cPct
        )

        val rawScores = mapOf(
            "Realistic" to rTotal,
            "Investigative" to iTotal,
            "Artistic" to aTotal,
            "Social" to sTotal,
            "Enterprising" to eTotal,
            "Conventional" to cTotal
        )

        // Find primary dominant RIASEC dimension
        val categoryScores = listOf(
            Triple("Realistic", "R", rTotal),
            Triple("Investigative", "I", iTotal),
            Triple("Artistic", "A", aTotal),
            Triple("Social", "S", sTotal),
            Triple("Enterprising", "E", eTotal),
            Triple("Conventional", "C", cTotal)
        ).sortedByDescending { it.third }

        val dominant = categoryScores.first()
        val dominantCode = dominant.second

        return when (dominantCode) {
            "R" -> PsychometricAssessmentResult(
                archetype = "Realistic — Practical & Hands-On Problem Solver",
                tag = "R • REALISTIC PATHWAY",
                riasecCode = "R",
                archetypeDescription = "Involve work activities that include practical, hands-on problems and solutions. You deal effectively with real-world materials, mechanical tools, gadgets, and outdoor systems, and excel at systematic step-by-step procedures.",
                topStrengths = listOf(
                    "Hands-on Building & Crafting",
                    "Gadget & Machinery Diagnostics",
                    "Systematic Step-by-Step Execution",
                    "Outdoor & Physical Resilience",
                    "Technical Troubleshooting"
                ),
                recommendedCareers = listOf(
                    "Robotics & Mechanical Engineer",
                    "Computer Hardware / Systems Specialist",
                    "Agricultural Technologist",
                    "Construction & Infrastructure Manager",
                    "Machinist & Precision Toolmaker",
                    "Hospitality & Food Operations Lead"
                ),
                recommendedCourses = listOf(
                    "Engineering (B.Tech / Mechanical / Civil / CS)",
                    "Agriculture (B.Sc Agri)",
                    "Computers (BCA / B.Sc CS / Hardware)",
                    "Construction Technology",
                    "Food and Hospitality (BHM / Catering)",
                    "Health Assistant / Allied Medical Tech"
                ),
                dimensionScores = dimensionScores,
                rawScores = rawScores,
                totalRiasecScore = totalPoints
            )

            "I" -> PsychometricAssessmentResult(
                archetype = "Investigative — Analytical & Scientific Thinker",
                tag = "I • INVESTIGATIVE PATHWAY",
                riasecCode = "I",
                archetypeDescription = "Involve working with ideas and require an extensive amount of thinking. You love searching for facts, analyzing situations for pros and cons, making rational decisions, and solving complex problems mentally.",
                topStrengths = listOf(
                    "Scientific Curiosity & Research",
                    "Rational Decision Making",
                    "Root Cause & Pros-Cons Analysis",
                    "Data Analytics & Mathematical Logic",
                    "Complex Problem Solving"
                ),
                recommendedCareers = listOf(
                    "Doctor / Surgeon (MBBS / MD)",
                    "AI & Machine Learning Scientist",
                    "Marine Biologist / Life Scientist",
                    "Research Chemist / Pharmacologist",
                    "Clinical Psychologist",
                    "Consumer Economics / Market Strategist"
                ),
                recommendedCourses = listOf(
                    "Medicine / Surgery (MBBS / BDS / BAMS)",
                    "Engineering (AI / Data Science / Biomedical)",
                    "Marine Biology & Zoology",
                    "Chemistry / Biochemistry",
                    "Psychology (B.Sc / M.Sc)",
                    "Consumer Economics & Econometrics"
                ),
                dimensionScores = dimensionScores,
                rawScores = rawScores,
                totalRiasecScore = totalPoints
            )

            "A" -> PsychometricAssessmentResult(
                archetype = "Artistic — Creative & Expressive Visionary",
                tag = "A • ARTISTIC PATHWAY",
                riasecCode = "A",
                archetypeDescription = "Involve working with forms, designs, and patterns. You thrive in unstructured situations where you can use your imagination, think outside the box, and express bold, aesthetic ideas without rigid constraints.",
                topStrengths = listOf(
                    "Spatial Imagination & Visual Design",
                    "Out-of-the-Box Innovation",
                    "Emotional Intelligence & Expression",
                    "Aesthetic & Multimedia Storytelling",
                    "Daring Creative Experimentation"
                ),
                recommendedCareers = listOf(
                    "Principal Architect (B.Arch)",
                    "UI/UX & Digital Product Designer",
                    "Media Director / Film & TV Producer",
                    "Interior & Sustainable Space Designer",
                    "Professional Photographer & Visual Artist",
                    "Brand Communications & Creative Lead"
                ),
                recommendedCourses = listOf(
                    "Architecture (B.Arch)",
                    "Communications & Mass Media (BA / BJMC)",
                    "Interior Design (B.Des / B.Sc)",
                    "Fine and Performing Arts (BFA)",
                    "Photography & Digital Cinematography",
                    "Radio and TV Broadcasting"
                ),
                dimensionScores = dimensionScores,
                rawScores = rawScores,
                totalRiasecScore = totalPoints
            )

            "S" -> PsychometricAssessmentResult(
                archetype = "Social — Empathetic Helper & Mentor",
                tag = "S • SOCIAL PATHWAY",
                riasecCode = "S",
                archetypeDescription = "Involve working with, communicating with, and teaching people. You find fulfillment in helping people in need (poor, disabled, sick), fostering team harmony, and guiding others toward personal and health growth.",
                topStrengths = listOf(
                    "Deep Empathy & Active Compassion",
                    "Supportive Team Harmony",
                    "Counseling & Mentorship Acumen",
                    "Interpersonal Public Relations",
                    "Patience & Community Upliftment"
                ),
                recommendedCareers = listOf(
                    "Counselor / Clinical Psychotherapist",
                    "Nursing Specialist & Healthcare Administrator",
                    "Physiotherapist (BPT / MPT)",
                    "Academic Educator / Professor",
                    "Public Relations Director",
                    "Travel, Tourism & Hospitality Manager"
                ),
                recommendedCourses = listOf(
                    "Nursing (B.Sc Nursing)",
                    "Counseling & Applied Psychology",
                    "Physical Therapy (BPT)",
                    "Education (B.Ed / Integrated BA-B.Ed)",
                    "Public Relations & Advertising",
                    "Travel & Tourism Management (BTTM)"
                ),
                dimensionScores = dimensionScores,
                rawScores = rawScores,
                totalRiasecScore = totalPoints
            )

            "E" -> PsychometricAssessmentResult(
                archetype = "Enterprising — Persuasive Leader & Strategist",
                tag = "E • ENTERPRISING PATHWAY",
                riasecCode = "E",
                archetypeDescription = "Involve starting up and carrying out projects, leading people, making high-stakes decisions, taking calculated risks, and driving commercial business ventures with infectious enthusiasm.",
                topStrengths = listOf(
                    "Persuasive Influence & Negotiation",
                    "Strategic Risk-Taking",
                    "Entrepreneurial Initiative",
                    "Commercial Foresight & Market Acumen",
                    "Inspirational Leadership"
                ),
                recommendedCareers = listOf(
                    "Corporate Entrepreneur / CEO",
                    "Corporate Lawyer / Legal Counsel (BA LLB)",
                    "Investment Banker & Wealth Strategist",
                    "Marketing, Brand & Sales Director",
                    "International Trade & Policy Consultant",
                    "Real Estate & Commercial Asset Lead"
                ),
                recommendedCourses = listOf(
                    "Law (BA LLB / BBA LLB Integrated)",
                    "Marketing / Sales (BBA / Integrated MBA)",
                    "Banking / Finance (B.Com Hons / CFA)",
                    "International Trade & Global Business",
                    "Political Science & Public Governance",
                    "Fashion Merchandising & Retail Management"
                ),
                dimensionScores = dimensionScores,
                rawScores = rawScores,
                totalRiasecScore = totalPoints
            )

            else -> PsychometricAssessmentResult(
                archetype = "Conventional — Methodical & Detail-Oriented Organizer",
                tag = "C • CONVENTIONAL PATHWAY",
                riasecCode = "C",
                archetypeDescription = "Involve following set procedures and routines, working with precise calculations, data, records, and regulatory compliance. You value accuracy, systematic order, and structured organizational integrity.",
                topStrengths = listOf(
                    "Precise Calculations & Financial Audit",
                    "Methodical Detail Orientation",
                    "Regulatory & Governance Compliance",
                    "Data Architecture & Record Governance",
                    "Systematic Operational Discipline"
                ),
                recommendedCareers = listOf(
                    "Chartered Accountant (CA / ACCA / CPA)",
                    "Financial Risk Analyst & Auditor",
                    "Banking Operations & Treasury Officer",
                    "Data Processing & Systems Architect",
                    "Medical Records & Health Informatics Lead",
                    "Corporate Governance & Administration Lead"
                ),
                recommendedCourses = listOf(
                    "Accounting (B.Com Professional + CA / CMA)",
                    "Data Processing & Business Analytics",
                    "Banking & Financial Services (B.Com / BAF)",
                    "Administration & Corporate Governance",
                    "Medical Records Administration",
                    "Court Reporting & Legal Documentation"
                ),
                dimensionScores = dimensionScores,
                rawScores = rawScores,
                totalRiasecScore = totalPoints
            )
        }
    }

    /**
     * Backward-compatible evaluation function supporting legacy string dimension lists.
     */
    fun evaluateAssessment(selectedOptionDimensions: List<String>): PsychometricAssessmentResult {
        // Map string selections to simulated RIASEC answers
        val map = mutableMapOf<Int, Int>()
        var realistic = 3
        var investigative = 3
        var artistic = 2
        var social = 2
        var enterprising = 2
        var conventional = 2

        for (dim in selectedOptionDimensions) {
            when {
                dim.contains("Technical", true) || dim.contains("Practical", true) -> realistic += 2
                dim.contains("Analytical", true) || dim.contains("Investigative", true) -> investigative += 2
                dim.contains("Creative", true) || dim.contains("Artistic", true) -> artistic += 2
                dim.contains("Healthcare", true) || dim.contains("Social", true) -> social += 2
                dim.contains("Business", true) || dim.contains("Enterprising", true) || dim.contains("Leadership", true) -> enterprising += 2
                dim.contains("Conventional", true) || dim.contains("Compliance", true) -> conventional += 2
            }
        }

        // Fill all 30 questions
        for (i in 1..5) map[i] = (realistic / 2).coerceIn(0, 4)
        for (i in 6..10) map[i] = (investigative / 2).coerceIn(0, 4)
        for (i in 11..15) map[i] = (artistic / 2).coerceIn(0, 4)
        for (i in 16..20) map[i] = (social / 2).coerceIn(0, 4)
        for (i in 21..25) map[i] = (enterprising / 2).coerceIn(0, 4)
        for (i in 26..30) map[i] = (conventional / 2).coerceIn(0, 4)

        return evaluateRiasecScores(map)
    }

    fun toDomainResult(entity: PsychometricAssessmentEntity): PsychometricAssessmentResult {
        val scores = linkedMapOf(
            "Realistic (R)" to if (entity.realisticScore > 0) entity.realisticScore else entity.technicalScore,
            "Investigative (I)" to if (entity.investigativeScore > 0) entity.investigativeScore else entity.analyticalScore,
            "Artistic (A)" to entity.artisticScore.let { if (it > 0) it else entity.creativeScore },
            "Social (S)" to entity.socialScore.let { if (it > 0) it else entity.healthcareScore },
            "Enterprising (E)" to entity.enterprisingScore.let { if (it > 0) it else entity.businessScore },
            "Conventional (C)" to entity.conventionalScore.let { if (it > 0) it else entity.leadershipScore }
        )

        val riasecCode = when {
            entity.archetype.contains("Realistic", true) -> "R"
            entity.archetype.contains("Investigative", true) -> "I"
            entity.archetype.contains("Artistic", true) -> "A"
            entity.archetype.contains("Social", true) -> "S"
            entity.archetype.contains("Enterprising", true) -> "E"
            else -> "C"
        }

        return PsychometricAssessmentResult(
            archetype = entity.archetype,
            tag = "${riasecCode} • RIASEC CAREER ASSESSMENT",
            riasecCode = riasecCode,
            archetypeDescription = entity.archetypeDescription,
            topStrengths = entity.topStrengths.split(", ").filter { it.isNotBlank() },
            recommendedCareers = entity.recommendedCareers.split(", ").filter { it.isNotBlank() },
            recommendedCourses = entity.recommendedCourses.split(", ").filter { it.isNotBlank() },
            dimensionScores = scores,
            totalRiasecScore = entity.totalScore
        )
    }
}
