package com.example.data.repository

import com.example.data.local.FavoriteEntity
import com.example.data.local.RecentEntity
import com.example.data.local.ToolDao
import com.example.data.model.AiTool
import com.example.data.model.ToolCategory
import com.example.data.network.GeminiApiClient
import kotlinx.coroutines.flow.Flow

class ToolRepository(private val toolDao: ToolDao) {

    val favoriteToolIds: Flow<List<String>> = toolDao.getAllFavoriteIds()
    val recentHistory: Flow<List<RecentEntity>> = toolDao.getRecentHistory()

    suspend fun isFavorite(toolId: String): Boolean = toolDao.isFavorite(toolId)

    suspend fun toggleFavorite(toolId: String) {
        if (toolDao.isFavorite(toolId)) {
            toolDao.removeFavorite(toolId)
        } else {
            toolDao.addFavorite(FavoriteEntity(toolId))
        }
    }

    suspend fun saveRecent(toolId: String, toolTitle: String, prompt: String, result: String, lang: String) {
        toolDao.addRecent(
            RecentEntity(
                toolId = toolId,
                toolTitle = toolTitle,
                promptText = prompt,
                resultText = result,
                languageCode = lang
            )
        )
    }

    suspend fun deleteRecent(id: Long) = toolDao.deleteRecent(id)
    suspend fun clearHistory() = toolDao.clearHistory()

    suspend fun generateAiResult(
        tool: AiTool,
        userPrompt: String,
        isBangla: Boolean
    ): Result<String> {
        val systemInstruction = if (isBangla) tool.systemInstructionBn else tool.systemInstructionEn
        val targetLang = if (isBangla) "Bangla (বাংলা)" else "English"
        return GeminiApiClient.generateContent(
            systemInstruction = systemInstruction,
            userPrompt = userPrompt,
            targetLanguage = targetLang
        )
    }

    fun getCategories(): List<ToolCategory> = listOf(
        ToolCategory(
            id = "writing",
            nameEn = "Writing",
            nameBn = "লেখালেখি",
            iconName = "Edit",
            colorHex = "#6366F1",
            descriptionEn = "Blogs, emails, essays & creative text generation",
            descriptionBn = "ব্লগ, ইমেইল, প্রবন্ধ ও সৃজনশীল লেখা"
        ),
        ToolCategory(
            id = "students",
            nameEn = "Students",
            nameBn = "শিক্ষার্থী",
            iconName = "School",
            colorHex = "#10B981",
            descriptionEn = "Homework, math solver, summary & study tools",
            descriptionBn = "হোমওয়ার্ক, গণিত সমাধান ও পড়ার কৌশল"
        ),
        ToolCategory(
            id = "youtube",
            nameEn = "YouTube",
            nameBn = "ইউটিউব",
            iconName = "PlayArrow",
            colorHex = "#EF4444",
            descriptionEn = "Scripts, titles, SEO tags, shorts & hooks",
            descriptionBn = "ভিডিও স্ক্রিপ্ট, টাইটেল, এসইও ও হুক্স"
        ),
        ToolCategory(
            id = "business",
            nameEn = "Business",
            nameBn = "ব্যবসা",
            iconName = "BusinessCenter",
            colorHex = "#F59E0B",
            descriptionEn = "Business names, slogans, ads & proposal writers",
            descriptionBn = "ব্যবসার নাম, স্লোগান, বিজ্ঞাপন ও প্রপোজাল"
        ),
        ToolCategory(
            id = "programming",
            nameEn = "Programming",
            nameBn = "প্রোগ্রামিং",
            iconName = "Code",
            colorHex = "#3B82F6",
            descriptionEn = "Code generators, debuggers, HTML, CSS & SQL",
            descriptionBn = "কোড জেনারেটর, ডিবাগার, এইচটিএমএল ও ব্যাকএন্ড"
        ),
        ToolCategory(
            id = "social",
            nameEn = "Social Media",
            nameBn = "সোশ্যাল মিডিয়া",
            iconName = "Share",
            colorHex = "#EC4899",
            descriptionEn = "Posts, captions, bios, reel scripts & trends",
            descriptionBn = "পোস্ট, ক্যাপশন, বায়ো, রিল স্ক্রিপ্ট ও ট্রেন্ড"
        ),
        ToolCategory(
            id = "image_prompt",
            nameEn = "Image Prompt",
            nameBn = "ইমেজ প্রম্পট",
            iconName = "Image",
            colorHex = "#8B5CF6",
            descriptionEn = "Prompts for Midjourney, DALL-E & Stable Diffusion",
            descriptionBn = "মিডজার্নি, ডাল-ই ও এআই ছবির প্রম্পট"
        ),
        ToolCategory(
            id = "career",
            nameEn = "Career",
            nameBn = "ক্যারিয়ার",
            iconName = "Work",
            colorHex = "#14B8A6",
            descriptionEn = "Interview prep, resume review, advice & negotiation",
            descriptionBn = "ইন্টারভিউ প্রস্তুতি, সিভি রিভিউ ও ক্যারিয়ার পরামর্শ"
        ),
        ToolCategory(
            id = "daily",
            nameEn = "Daily Life",
            nameBn = "দৈনন্দিন জীবন",
            iconName = "Schedule",
            colorHex = "#06B6D4",
            descriptionEn = "Meal plans, workouts, budget & habit tracking",
            descriptionBn = "মিল প্ল্যান, ওয়ার্কআউট, বাজেট ও লাইফস্টাইল"
        ),
        ToolCategory(
            id = "fun",
            nameEn = "Fun & Entertainment",
            nameBn = "বিনোদন",
            iconName = "SentimentSatisfied",
            colorHex = "#F43F5E",
            descriptionEn = "Jokes, riddles, roasts, quizzes & fantasy names",
            descriptionBn = "কৌতুক, ধাঁধা, রোস্ট, কুইজ ও মজার আইডিয়া"
        )
    )

    fun getAllTools(): List<AiTool> = buildList {
        // --- CATEGORY 1: WRITING (10 Tools) ---
        add(AiTool(
            id = "writing_blog",
            categoryId = "writing",
            titleEn = "Blog Writer",
            titleBn = "ব্লগ রাইটার",
            descriptionEn = "Generate SEO-optimized blog posts on any topic",
            descriptionBn = "যেকোনো বিষয়ের উপর এসইও ফ্রেন্ডলি ব্লগ পোস্ট তৈরি করুন",
            iconName = "Article",
            systemInstructionEn = "You are an expert SEO blog post writer. Generate high-quality, engaging blog posts with introduction, subheadings (H2, H3), bullet points, and conclusion.",
            systemInstructionBn = "আপনি একজন দক্ষ এসইও ব্লগ রাইটার। ভূমিকা, সাবহেডিং, বুলেট পয়েন্ট ও উপসংহার সহ আকর্ষণীয় ব্লগ পোস্ট লিখুন।",
            promptPlaceholderEn = "e.g., 5 Proven Strategies for Small Business Marketing in 2026",
            promptPlaceholderBn = "যেমন: ২০২৬ সালে ছোট ব্যবসার মার্কেটিং করার ৫টি টিপস",
            promptExamplesEn = listOf("5 Proven Strategies for Small Business Marketing", "How Artificial Intelligence is Changing Education", "Top 10 Healthy Morning Habits for Energy"),
            promptExamplesBn = listOf("ছোট ব্যবসার অনলাইন মার্কেটিং করার উপায়", "আর্টিফিশিয়াল ইন্টেলিজেন্স কীভাবে শিক্ষা বদলে দিচ্ছে", "সকালে ওঠার ৫টি অবিশ্বাস্য স্বাস্থ্য উপকারিতা"),
            tags = listOf("popular", "trending")
        ))
        add(AiTool(
            id = "writing_email",
            categoryId = "writing",
            titleEn = "Email Writer",
            titleBn = "ইমেইল রাইটার",
            descriptionEn = "Write professional, formal or friendly emails",
            descriptionBn = "প্রফেশনাল, প্রাতিষ্ঠানিক বা বন্ধুত্বপূর্ণ ইমেইল তৈরি করুন",
            iconName = "Email",
            systemInstructionEn = "You are a professional communication expert. Write persuasive, concise, and structured emails with suitable subject lines.",
            systemInstructionBn = "আপনি প্রফেশনাল কমিউনিকেশন বিশেষজ্ঞ। উপযুক্ত সাবজেক্ট লাইন সহ স্পষ্ট ও প্রাতিষ্ঠানিক ইমেইল লিখুন।",
            promptPlaceholderEn = "e.g., Requesting 3 days leave from work due to family function",
            promptPlaceholderBn = "যেমন: পারিবারিক প্রয়জনে ৩ দিনের ছুটির আবেদন করে বসকে ইমেইল",
            promptExamplesEn = listOf("Requesting a salary review meeting", "Follow up on a job interview", "Apology to client for service delay"),
            promptExamplesBn = listOf("চাকরির ইন্টারভিউ এর পর ফলোআপ ইমেইল", "পণ্য দেরিতে পৌঁছানোর জন্য ক্লায়েন্টের কাছে ক্ষমা প্রার্থনার ইমেইল", "নতুন ক্লায়েন্টকে প্রজেক্টের প্রস্তাব পাঠানো"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "writing_story",
            categoryId = "writing",
            titleEn = "Story Writer",
            titleBn = "গল্প লেখক",
            descriptionEn = "Create captivating fiction, short stories or fairy tales",
            descriptionBn = "আকর্ষণীয় কাল্পনিক গল্প, ছোটগল্প বা রূপকথা লিখুন",
            iconName = "AutoStories",
            systemInstructionEn = "You are a creative author. Craft immersive stories with rich dialogue, character development, suspense, and emotional depth.",
            systemInstructionBn = "আপনি একজন সৃজনশীল গল্পকার। প্রাণবন্ত বর্ণনা, চরিত্র চিত্রণ ও রোমাঞ্চকর প্লট সহ গল্প রচনা করুন।",
            promptPlaceholderEn = "e.g., A time traveler visits Dhaka in the year 2080",
            promptPlaceholderBn = "যেমন: ২০৮০ সালের ঢাকায় একজন টাইম ট্রাভেলারের অভিজ্ঞতা",
            promptExamplesEn = listOf("A mystery in a quiet seaside town", "A robot who secretly learned to paint", "An ancient key found in a grandmother's attic"),
            promptExamplesBn = listOf("একটি পুরোনো রাজবাড়ির রহস্যময় দরজার গল্প", "একজন তরুণ বিজ্ঞানী এবং তার নতুন আবিষ্কারের গল্প", "এক জাদুকরী বনের রূপকথা"),
            tags = listOf("trending")
        ))
        add(AiTool(
            id = "writing_essay",
            categoryId = "writing",
            titleEn = "Essay Writer",
            titleBn = "রচনা লেখক",
            descriptionEn = "Write well-structured academic essays with strong arguments",
            descriptionBn = "সুসংগঠিত একাডেমিক রচনা ও বিশ্লেষণমূলক প্রবন্ধ লিখুন",
            iconName = "HistoryEdu",
            systemInstructionEn = "You are an academic essay specialist. Write well-researched, persuasive essays with thesis statement, body paragraphs, and strong conclusion.",
            systemInstructionBn = "আপনি একজন একাডেমিক রচনা বিশেষজ্ঞ। শক্তিশালী পয়েন্ট ও যৌক্তিক যুক্তিসহ তথ্যবহুল রচনা লিখুন।",
            promptPlaceholderEn = "e.g., Impact of Climate Change on Developing Nations",
            promptPlaceholderBn = "যেমন: পরিবেশ সুরক্ষায় তরুণদের ভূমিকা",
            promptExamplesEn = listOf("The Ethics of Artificial Intelligence", "Benefits of Renewable Energy Transition", "Digital Literacy in Modern Education"),
            promptExamplesBn = listOf("বাংলাদেশে ডিজিটাল বিপ্লব ও তার প্রভাব", "পরিবেশ দূষণ প্রতিরোধে আমাদের করণীয়", "দৈনন্দিন জীবনে বিজ্ঞানের অবদান"),
            tags = listOf("newest")
        ))
        add(AiTool(
            id = "writing_grammar",
            categoryId = "writing",
            titleEn = "Grammar Fixer",
            titleBn = "ব্যাকরণ সংশোধন",
            descriptionEn = "Correct grammar, spelling, punctuation & phrasing",
            descriptionBn = "বানান, ব্যাকরণ ও বাক্য গঠন নিখুঁত করুন",
            iconName = "Spellcheck",
            systemInstructionEn = "You are a master editor. Correct all grammatical, spelling, and stylistic errors in the text. Provide the corrected text first, then brief explanation of key fixes.",
            systemInstructionBn = "আপনি একজন অভিজ্ঞ সম্পাদক। সকল ব্যাকরণ ও বানানের ভুল সংশোধন করে সঠিক রূপ ও ব্যাখ্যা প্রদান করুন।",
            promptPlaceholderEn = "Paste your raw text here to fix mistakes...",
            promptPlaceholderBn = "এখানে আপনার ভুল বাক্যটি পেস্ট করুন...",
            promptExamplesEn = listOf("he go to market yesterday and buyed three apple.", "Me and him went to the store for buying some cloths."),
            promptExamplesBn = listOf("আমি গতকাল ঢাকা গিয়াছিলাম এবং অনেক কেনাকাটা করলাম।", "তারা সকল ছাত্ররা আজকে স্কুলে উপস্থিত ছিল।"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "writing_resume",
            categoryId = "writing",
            titleEn = "Resume Writer",
            titleBn = "রিজিউম রাইটার",
            descriptionEn = "Create ATS-friendly professional resume summaries & bullet points",
            descriptionBn = "এটিএস ফ্রেন্ডলি প্রফেশনাল রিজিউম সামারি ও পয়েন্ট লিখুন",
            iconName = "Badge",
            systemInstructionEn = "You are a executive resume writer. Generate action-oriented, metrics-driven resume summaries and bullet points aligned with modern ATS standards.",
            systemInstructionBn = "আপনি সিভি বিশেষজ্ঞ। চাকরির জন্য আকর্ষণীয় ও প্রভাবশালী রিজিউম সেকশন ও সামারি লিখুন।",
            promptPlaceholderEn = "e.g., Software Engineer with 3 years React & Android experience",
            promptPlaceholderBn = "যেমন: ৩ বছরের অভিজ্ঞতাসম্পন্ন সফটওয়্যার ডেভেলপারের সিভি সেকশন",
            promptExamplesEn = listOf("Senior Marketing Manager with 5 years experience in e-commerce", "Data Analyst proficient in Python and SQL"),
            promptExamplesBn = listOf("ডিজিটাল মার্কেটিং এক্সিকিউটিভ হিসেবে সিভি সামারি", "কাস্টমার সাপোর্ট রিপ্রেজেন্টেটিভ এর কাজের বিবরণী"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "writing_coverletter",
            categoryId = "writing",
            titleEn = "Cover Letter",
            titleBn = "কভার লেটার",
            descriptionEn = "Tailor cover letters to specific job posts and companies",
            descriptionBn = "নির্দিষ্ট চাকরির জন্য আকর্ষণীয় কভার লেটার লিখুন",
            iconName = "MailOutline",
            systemInstructionEn = "You are a career consultant. Write tailored, persuasive cover letters matching job candidates with specific company roles.",
            systemInstructionBn = "আপনি ক্যারিয়ার পরামর্শক। নির্দিষ্ট জব রোল এর সাথে মিলিয়ে প্রফেশনাল কভার লেটার লিখুন।",
            promptPlaceholderEn = "e.g., Applying for Graphic Designer role at Creative Studio",
            promptPlaceholderBn = "যেমন: গ্রাফিক্স ডিজাইনার পদের জন্য কভার লেটার",
            promptExamplesEn = listOf("Applying for Junior Web Developer at tech startup", "Project Manager role at multinational logistics firm"),
            promptExamplesBn = listOf("কনটেন্ট রাইটার পদের জন্য আবেদনপত্র", "ব্যাংক অফিসারের পদের জন্য জব কভার লেটার"),
            tags = listOf("newest")
        ))
        add(AiTool(
            id = "writing_caption",
            categoryId = "writing",
            titleEn = "Caption Generator",
            titleBn = "ক্যাপশন জেনারেটর",
            descriptionEn = "Catchy social captions with hashtags & emojis",
            descriptionBn = "আকর্ষণীয় সোশ্যাল মিডিয়া ক্যাপশন, হ্যাশট্যাগ ও ইমোজি সহ",
            iconName = "FormatQuote",
            systemInstructionEn = "You are a viral social media writer. Generate creative, captivating captions with emojis and targeted trending hashtags.",
            systemInstructionBn = "আপনি সোশ্যাল মিডিয়া বিশেষজ্ঞ। ইমোজি ও প্রাসঙ্গিক হ্যাশট্যাগ সহ ভাইরাল ক্যাপশন লিখুন।",
            promptPlaceholderEn = "e.g., Sunset photo at Cox's Bazar beach",
            promptPlaceholderBn = "যেমন: কক্সবাজার সমুদ্র সৈকতে সন্ধ্যার ছবি",
            promptExamplesEn = listOf("New coffee shop opening in city center", "First day at my new dream job!"),
            promptExamplesBn = listOf("বন্ধুদের সাথে হঠাৎ কোনো সুন্দর সফরের ছবি", "নিজের প্রথম বাইক কেনার আনন্দঘন মুহূর্ত"),
            tags = listOf("trending")
        ))
        add(AiTool(
            id = "writing_rewrite",
            categoryId = "writing",
            titleEn = "Rewrite Tool",
            titleBn = "পুনরায় লিখুন",
            descriptionEn = "Paraphrase text in fluent, catchy or formal tone",
            descriptionBn = "যেকোনো লেখা ভিন্ন টোন বা সহজ ভাষায় রিরাইট করুন",
            iconName = "Autorenew",
            systemInstructionEn = "You are a professional paraphraser. Rewrite text to improve clarity, vocabulary, flow, or tone while preserving original meaning.",
            systemInstructionBn = "আপনি রিরাইটিং বিশেষজ্ঞ। মূল অর্থ ঠিক রেখে যেকোনো লেখাকে আরও সুন্দর ও সাবলীলভাবে পুনরায় লিখুন।",
            promptPlaceholderEn = "Paste original paragraph here to paraphrase...",
            promptPlaceholderBn = "এখানে মূল লেখাটি পেস্ট করুন রিরাইট করার জন্য...",
            promptExamplesEn = listOf("Our company strives to provide high quality solutions to customers everyday.", "Learning programming requires time and continuous practice."),
            promptExamplesBn = listOf("আমাদের লক্ষ্য হলো গ্রাহকদের সেরা সেবা দেওয়া এবং তাদের অভিজ্ঞতা সুন্দর করা।", "পড়াশোনায় ভালো করতে হলে প্রতিদিন নির্দিষ্ট সময় দেওয়া জরুরি।"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "writing_product",
            categoryId = "writing",
            titleEn = "Product Description",
            titleBn = "পণ্য বিবরণী",
            descriptionEn = "Persuasive e-commerce descriptions that boost sales",
            descriptionBn = "ই-কমার্স পণ্যের ইমপ্যাক্টফুল সেলস ডেসক্রিপশন",
            iconName = "ShoppingBag",
            systemInstructionEn = "You are an e-commerce copywriter. Write persuasive, feature-benefit driven product descriptions designed to maximize conversions.",
            systemInstructionBn = "আপনি ই-কমার্স কপিরাইটার। পণ্যের বৈশিষ্ট্য ও উপকারিতা তুলে ধরে বিক্রয় বৃদ্ধিকারী আকর্ষণীয় বর্ণনা লিখুন।",
            promptPlaceholderEn = "e.g., Wireless Noise-Canceling Bluetooth Headphones with 30hr battery",
            promptPlaceholderBn = "যেমন: নয়েজ ক্যানসেলিং ওয়্যারলেস হেডফোন",
            promptExamplesEn = listOf("Organic Handmade Lavender Soap Bar", "Ergonomic Mesh Office Chair with Lumbar Support"),
            promptExamplesBn = listOf("খাঁটি সুন্দরবনের মধু (৫০০ গ্রাম)", "প্রিমিয়াম কোয়ালিটির লেদার মানিব্যাগ"),
            tags = listOf("newest")
        ))

        // --- CATEGORY 2: STUDENTS (10 Tools) ---
        add(AiTool(
            id = "students_homework",
            categoryId = "students",
            titleEn = "Homework Helper",
            titleBn = "হোমওয়ার্ক হেলপার",
            descriptionEn = "Step-by-step guidance for any subject or assignment",
            descriptionBn = "যেকোনো বিষয়ের হোমওয়ার্কের সহজ ব্যাখ্যা ও সমাধান",
            iconName = "MenuBook",
            systemInstructionEn = "You are a patient academic tutor. Solve homework queries step-by-step with clear explanations suited for students.",
            systemInstructionBn = "আপনি একজন ধৈর্যশীল টিউটর। শিক্ষার্থীদের জন্য সহজ ভাষায় ধাপে ধাপে প্রশ্নের সমাধান বুঝিয়ে দিন।",
            promptPlaceholderEn = "e.g., Explain Newton's Three Laws of Motion with real examples",
            promptPlaceholderBn = "যেমন: সালোকসংশ্লেষণ প্রক্রিয়া কী এবং কীভাবে ঘটে তা সহজ ভাষায় ব্যাখ্যা কর",
            promptExamplesEn = listOf("Why did the Industrial Revolution start in Britain?", "Explain Photosynthesis step by step"),
            promptExamplesBn = listOf("নিউটন এর গতির তিনটি সূত্র সহজ উদাহরণের মাধ্যমে ব্যাখ্যা কর", "ফরাসি বিপ্লবের প্রধান কারণসমূহ কী ছিল?"),
            tags = listOf("popular", "trending")
        ))
        add(AiTool(
            id = "students_math",
            categoryId = "students",
            titleEn = "Math Solver",
            titleBn = "গণিত সমাধান",
            descriptionEn = "Solve math equations with full step-by-step steps",
            descriptionBn = "গণিতের জটিল সমীকরণ ও অংকের সমাধান ধাপে ধাপে",
            iconName = "Functions",
            systemInstructionEn = "You are a expert math professor. Solve math problems step-by-step, explaining formulas, rules, and calculations clearly.",
            systemInstructionBn = "আপনি গণিত বিশেষজ্ঞ। সমীকরণ ও অংকগুলোর বিস্তারিত সমাধান ও সূত্রসহ ধাপে ধাপে ব্যাখ্যা প্রদান করুন।",
            promptPlaceholderEn = "e.g., Solve 2x^2 + 5x - 3 = 0",
            promptPlaceholderBn = "যেমন: ৩x + ৫ = ২০ হলে x এর মান কত এবং কীভাবে বের করবে?",
            promptExamplesEn = listOf("Find derivative of f(x) = x^3 * sin(x)", "Calculate compound interest for $1000 at 5% for 3 years"),
            promptExamplesBn = listOf("সমকোণী ত্রিভুজের অতিভুজ ১০ সেমি এবং ভূমি ৮ সেমি হলে লম্ব কত?", "বীজগণিতীয় সূত্রের সাহায্যে (a+b)^2 এর প্রমাণ"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "students_summary",
            categoryId = "students",
            titleEn = "Summary Generator",
            titleBn = "সারসংক্ষেপ",
            descriptionEn = "Summarize long articles, chapters or text into key points",
            descriptionBn = "দীর্ঘ অনুচ্ছেদ বা অধ্যায়ের মূলকথা সংক্ষেপে বের করুন",
            iconName = "Compress",
            systemInstructionEn = "You are a study summarizer. Condense long texts into concise bullet points, executive summaries, and core takeaways.",
            systemInstructionBn = "আপনি তথ্য সংক্ষিপ্তকরণ বিশেষজ্ঞ। যেকোনো বড় টেক্সটের মূল পয়েন্ট ও সারসংক্ষেপ তৈরি করুন।",
            promptPlaceholderEn = "Paste long article text here...",
            promptPlaceholderBn = "এখানে আপনার মূল অধ্যায় বা টেক্সট পেস্ট করুন...",
            promptExamplesEn = listOf("Paste article about Quantum Computing basics", "Paste chapter summary request on World War II timeline"),
            promptExamplesBn = listOf("জলবায়ু পরিবর্তন সম্পর্কিত অনুচ্ছেদের সারসংক্ষেপ", "কম্পিউটার নেটওয়ার্কিং এর প্রকারভেদের সামারি"),
            tags = listOf("trending")
        ))
        add(AiTool(
            id = "students_mcq",
            categoryId = "students",
            titleEn = "MCQ Generator",
            titleBn = "MCQ জেনারেটর",
            descriptionEn = "Create multiple choice questions with answers & options",
            descriptionBn = "যেকোনো টপিক থেকে বহুনির্বাচনী প্রশ্ন ও উত্তর বানান",
            iconName = "CheckCircleOutline",
            systemInstructionEn = "You are an exam creator. Generate high-quality Multiple Choice Questions (MCQs) with options (A, B, C, D) and answer keys with explanations.",
            systemInstructionBn = "আপনি পরীক্ষার প্রশ্ন তৈরিকারক। ৪টি অপশন এবং সঠিক উত্তর ব্যাখ্যাসহ বহুনির্বাচনী প্রশ্ন তৈরি করুন।",
            promptPlaceholderEn = "e.g., Generate 5 MCQs on Human Heart Anatomy",
            promptPlaceholderBn = "যেমন: বাংলাদেশের ইতিহাস ও মুক্তিযুদ্ধ নিয়ে ৫টি MCQ প্রশ্ন বানান",
            promptExamplesEn = listOf("Generate 5 MCQs on Periodic Table Elements", "Generate 5 MCQs on World Geography"),
            promptExamplesBn = listOf("তথ্য ও যোগাযোগ প্রযুক্তি থেকে ৫টি বহুনির্বাচনী প্রশ্ন", "পদার্থবিজ্ঞানের গতি অধ্যায় থেকে ৫টি MCQ"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "students_quiz",
            categoryId = "students",
            titleEn = "Quiz Generator",
            titleBn = "কুইজ জেনারেটর",
            descriptionEn = "Interactive short answer and fill-in-the-blank quizzes",
            descriptionBn = "সংক্ষিপ্ত প্রশ্ন ও শূন্যস্থান পূরণের কুইজ তৈরি করুন",
            iconName = "Quiz",
            systemInstructionEn = "You are a quiz master. Create interactive study quizzes with short questions, fill-in-the-blanks, and answer keys.",
            systemInstructionBn = "আপনি কুইজ মাস্টার। শিক্ষার্থীদের জন্য কুইজ সেট এবং উত্তরপত্র তৈরি করুন।",
            promptPlaceholderEn = "e.g., Quiz on Solar System for Grade 8 students",
            promptPlaceholderBn = "যেমন: অষ্টম শ্রেণীর সাধারণ বিজ্ঞান থেকে ১০ নম্বরের কুইজ",
            promptExamplesEn = listOf("Short quiz on English Tenses and Grammar", "Quiz on Fundamental Physics Constants"),
            promptExamplesBn = listOf("বাংলা ব্যাকরণের সমাস নিয়ে কুইজ", "বিশ্বের বিভিন্ন দেশের রাজধানী নিয়ে কুইজ"),
            tags = listOf("newest")
        ))
        add(AiTool(
            id = "students_flashcards",
            categoryId = "students",
            titleEn = "Flashcards",
            titleBn = "ফ্ল্যাশকার্ড",
            descriptionEn = "Q&A flashcards for memorization & revision",
            descriptionBn = "সহজে মনে রাখার জন্য প্রশ্নোত্তর ফ্ল্যাশকার্ড",
            iconName = "Style",
            systemInstructionEn = "You are a memory coach. Generate Front/Back term-and-definition flashcards optimized for spaced repetition learning.",
            systemInstructionBn = "আপনি স্মৃতিনিয়ন্ত্রণ প্রশিক্ষক। সহজে মুখস্থ ও রিভিশনের জন্য কার্ড স্টাইলে ফ্রন্ট/ব্যাক প্রশ্নোত্তর বানান।",
            promptPlaceholderEn = "e.g., 10 flashcards for SAT English Vocabulary",
            promptPlaceholderBn = "যেমন: ইংরেজি ভোকাবুলারি মনে রাখার জন্য ১০টি ফ্ল্যাশকার্ড",
            promptExamplesEn = listOf("10 flashcards for Organic Chemistry functional groups", "10 flashcards for French greetings and basic phrases"),
            promptExamplesBn = listOf("আইসিটি কম্পিউটার পার্টস ফ্ল্যাশকার্ড", "জীববিজ্ঞান কোষের অঙ্গাণু ফ্ল্যাশকার্ড"),
            tags = listOf("trending")
        ))
        add(AiTool(
            id = "students_planner",
            categoryId = "students",
            titleEn = "Study Planner",
            titleBn = "পড়াশোনার প্ল্যান",
            descriptionEn = "Personalized exam routine & study timetables",
            descriptionBn = "পরীক্ষার জন্য কাস্টমাইজড ডেইলি রুটিন ও স্টাডি প্ল্যান",
            iconName = "CalendarToday",
            systemInstructionEn = "You are an academic advisor. Build realistic, balanced study routines allocating preparation time across subjects.",
            systemInstructionBn = "আপনি শিক্ষা উপদেষ্টা। পরীক্ষার প্রস্তুতি ও প্রতিদিনের পড়াশোনার সময় ভাগ করে কার্যকর রুটিন বানিয়ে দিন।",
            promptPlaceholderEn = "e.g., 30-day preparation schedule for final exams with 5 subjects",
            promptPlaceholderBn = "যেমন: আগামী ৩০ দিনের জন্য পরীক্ষার প্রস্তুতির ডেইলি রুটিন",
            promptExamplesEn = listOf("Weekly timetable balancing math, science, and literature", "7-day intensive revision plan before test"),
            promptExamplesBn = listOf("এইচএসসি পরীক্ষার জন্য ৩ মাসের মাস্টার প্ল্যান", "বিশ্ববিদ্যালয় ভর্তি পরীক্ষার প্রস্তুতি রুটিন"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "students_presentation",
            categoryId = "students",
            titleEn = "Presentation Maker",
            titleBn = "প্রেজেন্টেশন মেকার",
            descriptionEn = "Slide outlines, slide titles, and speaker notes",
            descriptionBn = "স্লাইডের আউটলাইন, কন্টেন্ট ও স্পিকার নোটস তৈরি করুন",
            iconName = "Slideshow",
            systemInstructionEn = "You are a presentation designer. Structure multi-slide presentation decks with Slide Titles, Bullet Points, and Speaker Notes.",
            systemInstructionBn = "আপনি প্রেজেন্টেশন বিশেষজ্ঞ। পাওয়ারপয়েন্ট স্লাইডের স্ট্রাকচার, টাইটেল, পয়েন্ট ও স্পিকার নোটস বানান।",
            promptPlaceholderEn = "e.g., 6 slide outline on Renewable Energy vs Fossil Fuels",
            promptPlaceholderBn = "যেমন: আর্টিফিশিয়াল ইন্টেলিজেন্স এর ভবিষ্যৎ নিয়ে ৫ স্লাইডের প্রেজেন্টেশন",
            promptExamplesEn = listOf("10 slide presentation on Global Supply Chain Management", "5 slide presentation on Cyber Security Awareness"),
            promptExamplesBn = listOf("পরিবেশ রক্ষায় পলিথিন বর্জনের উপর স্লাইড আউটলাইন", "উদ্যোক্তা হওয়ার প্রথম ধাপ নিয়ে উপস্থাপনা"),
            tags = listOf("newest")
        ))
        add(AiTool(
            id = "students_notes",
            categoryId = "students",
            titleEn = "Notes Generator",
            titleBn = "নোটস জেনারেটর",
            descriptionEn = "Format lectures or topics into clean structured notes",
            descriptionBn = "লোকচার বা টপিক থেকে সুন্দর গুছানো হ্যান্ডনোট বানান",
            iconName = "NoteAlt",
            systemInstructionEn = "You are a top student note-taker. Convert raw topic descriptions or transcripts into structured, clear, Cornell-style study notes.",
            systemInstructionBn = "আপনি মেধাবী নোট প্রস্তুতকারক। যেকোন জটিল বিষয়কে সহজ ও আকর্ষণীয় হ্যান্ডনोटे রূপান্তর করুন।",
            promptPlaceholderEn = "e.g., Create study notes on Cell Division (Mitosis vs Meiosis)",
            promptPlaceholderBn = "যেমন: মহাকর্ষ ও অভিকর্ষ বলের বিস্তারিত হ্যান্ডনোট",
            promptExamplesEn = listOf("Notes on Keynesian Economics vs Classical Economics", "Notes on Structure of DNA and RNA"),
            promptExamplesBn = listOf("বাংলাদেশের সংবিধানের মূলনীতি সমূহের হ্যান্ডনোট", "পর্যায় সারণির বৈশিষ্ট্য ও গ্রুপ পরিচিতি নোট"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "students_explain",
            categoryId = "students",
            titleEn = "Explain Anything",
            titleBn = "সহজ ব্যাখ্যা",
            descriptionEn = "Explain complex topics like I'm 5 years old (ELI5)",
            descriptionBn = "যেকোনো কঠিন টপিক সহজ সাধারণ ভাষায় বুঝে নিন",
            iconName = "Psychology",
            systemInstructionEn = "You are an ELI5 (Explain Like I'm 5) educator. Break down extremely complex ideas into simple analogies, plain terms, and relatable stories.",
            systemInstructionBn = "আপনি একজন সহজভাষী শিক্ষক। জটিল থেকে জটিলতম বিষয়কে জাদুকরী সহজ রূপক ও গল্পের মাধ্যমে বুঝিয়ে বলুন।",
            promptPlaceholderEn = "e.g., How does Quantum Computing work?",
            promptPlaceholderBn = "যেমন: ব্লকচেইন টেকনোলজি আসলে কী এবং কীভাবে কাজ করে?",
            promptExamplesEn = listOf("Explain Relativity Theory simply", "Explain how Inflation affects money value"),
            promptExamplesBn = listOf("কৃত্রিম বুদ্ধিমত্তা কীভাবে চিন্তা করে সহজ ভাষায় বুঝাও", "ইন্টারনেট কীভাবে এক দেশ থেকে অন্য দেশে ডাটা পাঠায়?"),
            tags = listOf("popular", "trending")
        ))

        // --- CATEGORY 3: YOUTUBE (10 Tools) ---
        add(AiTool(
            id = "youtube_script",
            categoryId = "youtube",
            titleEn = "Script Generator",
            titleBn = "ভিডিও স্ক্রিপ্ট",
            descriptionEn = "Full YouTube video scripts with intro, timestamps & CTA",
            descriptionBn = "ইউটিউব ভিডিওর সম্পূর্ণ স্ক্রিপ্ট হুক ও টাইমস্ট্যাম্প সহ",
            iconName = "VideoCameraFront",
            systemInstructionEn = "You are a professional YouTube scriptwriter. Write engaging video scripts with Intro Hook, Content Sections, Visual Cues, and Call to Actions.",
            systemInstructionBn = "আপনি ইউটিউব স্ক্রিপ্ট রাইটার। আকর্ষণীয় ইন্ট্রো, কন্টেন্ট সেকশন, ভিজ্যুয়াল ডিরেকশন ও সাবস্ক্রাইব সিটিএ সহ স্ক্রিপ্ট লিখুন।",
            promptPlaceholderEn = "e.g., Top 5 Hidden Features of Samsung Galaxy S26",
            promptPlaceholderBn = "যেমন: কিভাবে ১০ দিনে ফ্রিল্যান্সিং শুরু করবেন - ভিডিও স্ক্রিপ্ট",
            promptExamplesEn = listOf("10 Minute YouTube video script on AI Video Tools", "Product Review script for M3 MacBook Air"),
            promptExamplesBn = listOf("কম টাকায় ভ্রমণের ৫টি সেরা টিপস - ইউটিউব ভিডিও স্ক্রিপ্ট", "ইউটিউব থেকে আয় করার ৩টি সহজ উপায়"),
            tags = listOf("popular", "trending")
        ))
        add(AiTool(
            id = "youtube_ideas",
            categoryId = "youtube",
            titleEn = "Video Ideas",
            titleBn = "ভিডিও আইডিয়া",
            descriptionEn = "Generate viral video concepts and content angles",
            descriptionBn = "ভাইরাল ইউটিউব কন্টেন্ট ও ইউনিক ভিডিও আইডিয়া",
            iconName = "Lightbulb",
            systemInstructionEn = "You are a YouTube growth strategist. Generate catchy, high-CTR video concepts and angle ideas based on a niche or keyword.",
            systemInstructionBn = "আপনি ইউটিউব চ্যানেল বৃদ্ধি বিশেষজ্ঞ। ট্রেন্ডিং ও আকর্ষণীয় ক্লিক-ওয়্যার্দি ভিডিও কন্টেন্ট আইডিয়া বের করুন।",
            promptPlaceholderEn = "e.g., Channel niche: Tech reviews & gadgets",
            promptPlaceholderBn = "যেমন: গেমিং ও টেকনোলজি ভিত্তিক ইউটিউব কন্টেন্ট আইডিয়া",
            promptExamplesEn = listOf("10 viral ideas for personal finance channel", "10 video ideas for cooking vlog channel"),
            promptExamplesBn = listOf("স্টুডেন্টদের জন্য ১০টি জনপ্রিয় ইউটিউব ভিডিও আইডিয়া", "ট্রাভেল ব্লগিং চ্যানেলের জন্য ইউনিক কন্টেন্ট"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "youtube_title",
            categoryId = "youtube",
            titleEn = "Title Generator",
            titleBn = "আকর্ষণীয় টাইটেল",
            descriptionEn = "High-CTR, catchy & SEO YouTube titles",
            descriptionBn = "ক্লিক রেট (CTR) বাড়ানো আকর্ষণীয় ইউটিউব শিরোনাম",
            iconName = "Title",
            systemInstructionEn = "You are a YouTube title strategist. Craft high-click-through-rate (CTR) titles that spark curiosity, emotion, and search relevance.",
            systemInstructionBn = "আপনি টাইটেল এক্সপার্ট। দর্শকদের ক্লিক করতে বাধ্য করবে এমন আকর্ষণীয় ও এসইও ফ্রেন্ডলি টাইটেল তৈরি করুন।",
            promptPlaceholderEn = "e.g., Video about learning Python programming in 2026",
            promptPlaceholderBn = "যেমন: পাইথন প্রোগ্রামিং শেখার উপায় নিয়ে ভিডিও টাইটেল",
            promptExamplesEn = listOf("Video about quitting social media for 30 days", "Video about building a $1000/mo side hustle"),
            promptExamplesBn = listOf("পুরানো ল্যাপটপ ফাস্ট করার সহজ উপায় - টাইটেল", "১ মাসে ইংরেজি শেখার জাদুকরী টিপস - টাইটেল"),
            tags = listOf("trending")
        ))
        add(AiTool(
            id = "youtube_tags",
            categoryId = "youtube",
            titleEn = "SEO Tags",
            titleBn = "এসইও ট্যাগস",
            descriptionEn = "Generate comma-separated YouTube SEO tags & keywords",
            descriptionBn = "ইউটিউব এসইও ট্যাগস ও কিওয়ার্ডস বের করুন",
            iconName = "Tag",
            systemInstructionEn = "You are a YouTube SEO expert. Generate comprehensive, highly searched comma-separated video tags and search keywords.",
            systemInstructionBn = "আপনি ইউটিউব এসইও এক্সপার্ট। ভিডিও র‍্যাঙ্ক করানোর উপযোগী কমা-দ্বারা পৃথকীকৃত শক্তিশালী এসইও ট্যাগস দিন।",
            promptPlaceholderEn = "e.g., Android app development tutorial for beginners",
            promptPlaceholderBn = "যেমন: গ্রাফিক্স ডিজাইন টিউটোরিয়াল বাংলা",
            promptExamplesEn = listOf("Best camera settings for YouTube vlogging", "How to edit videos in CapCut"),
            promptExamplesBn = listOf("অনলাইনে টাকা আয়ের সহজ উপায় এসইও ট্যাগস", "মোবাইল দিয়ে ভিডিও এডিটিং কোর্স ট্যাগস"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "youtube_description",
            categoryId = "youtube",
            titleEn = "Description Writer",
            titleBn = "বিবরণী লেখক",
            descriptionEn = "SEO friendly descriptions with timestamps, links & social tags",
            descriptionBn = "এসইও সমৃদ্ধ ভিডিও বিবরণী, সময়সূচি ও সামাজিক লিঙ্ক",
            iconName = "Description",
            systemInstructionEn = "You are a YouTube copywriter. Generate structured descriptions with summary, chapters/timestamps, links placeholder, and search terms.",
            systemInstructionBn = "আপনি ভিডিও কন্টেন্ট ডেসক্রিপশন রাইটার। সার্চ ফ্রেন্ডলি বর্ণনা, চ্যাপ্টার টাইমস্ট্যাম্প ও লিঙ্ক সেকশন সহ লিখুন।",
            promptPlaceholderEn = "e.g., Unboxing & Review of iPhone 17 Pro",
            promptPlaceholderBn = "যেমন: নতুন ল্যাপটপ আনবক্সিং এবং রিভিউ ভিডিও বিবরণী",
            promptExamplesEn = listOf("Full React JS crash course video description", "Budget travel vlog to Bali video description"),
            promptExamplesBn = listOf("ওয়েব ডেভেলপমেন্ট কোর্সের ইউটিউব ডেসক্রিপশন", "সহজে বিরিয়ানি রান্নার ভিডিও বিবরণী"),
            tags = listOf("newest")
        ))
        add(AiTool(
            id = "youtube_thumbnail",
            categoryId = "youtube",
            titleEn = "Thumbnail Text",
            titleBn = "থাম্বনেইল টেক্সট",
            descriptionEn = "Punchy 2-4 word thumbnail text concepts & visual ideas",
            descriptionBn = "থাম্বনেইলে ব্যবহারের উপযোগী সংক্ষিপ্ত ও আকর্ষণীয় টেক্সট",
            iconName = "SmartDisplay",
            systemInstructionEn = "You are a thumbnail art director. Provide 5 punchy (2-4 words) thumbnail text overlays and visual composition suggestions.",
            systemInstructionBn = "আপনি থাম্বনেইল ডিজাইনার। থাম্বনেইলে লেখার মতো মাত্র ২-৪ শব্দের আকর্ষণীয় টেক্সট ও ডিজাইনের আইডিয়া দিন।",
            promptPlaceholderEn = "e.g., Video showing how I lost 10kg in 1 month",
            promptPlaceholderBn = "যেমন: কিভাবে ১ মাসে ১০ কেজি ওজন কমালাম",
            promptExamplesEn = listOf("Don't Buy This Phone Until You Watch This!", "I Quit My Job at 25"),
            promptExamplesBn = listOf("ভুলও এই ফোন কিনবেন না!", "১ মাসে ১ লাখ টাকা ইনকাম!"),
            tags = listOf("trending")
        ))
        add(AiTool(
            id = "youtube_shorts",
            categoryId = "youtube",
            titleEn = "Shorts Script",
            titleBn = "শর্টস স্ক্রিপ্ট",
            descriptionEn = "60-second fast-paced scripts for YouTube Shorts & Reels",
            descriptionBn = "৬০ সেকেন্ডের দ্রুতগতির ইউটিউব শর্টস স্ক্রিপ্ট",
            iconName = "Subscriptions",
            systemInstructionEn = "You are a viral short-form scriptwriter. Write 30-60 second fast-paced, high retention Scripts with instant hooks.",
            systemInstructionBn = "আপনি শর্টস কন্টেন্ট রাইটার। প্রথম ৩ সেকেন্ডে দর্শক ধরে রাখার মতো ৬০ সেকেন্ডের গতিশীল স্ক্রিপ্ট লিখুন।",
            promptPlaceholderEn = "e.g., 3 mind-blowing facts about space in 45 seconds",
            promptPlaceholderBn = "যেমন: মহাকাশ সম্পর্কে ৩টি অবিশ্বাস্য তথ্য নিয়ে শর্টস",
            promptExamplesEn = listOf("Secret iPhone trick nobody knows", "3 life hacks for students"),
            promptExamplesBn = listOf("মোবাইলের গোপন ১টি ট্রিক যা কেউ জানে না", "সকালে দ্রুত ঘুম থেকে ওঠার সিক্রেট ট্রিক"),
            tags = listOf("popular", "trending")
        ))
        add(AiTool(
            id = "youtube_hooks",
            categoryId = "youtube",
            titleEn = "Hooks Generator",
            titleBn = "ভিডিও হুক",
            descriptionEn = "First 5-second attention grabbers that double watch time",
            descriptionBn = "ভিডিওর প্রথম ৫ সেকেন্ডে দর্শক আটকে রাখার জাদুকরী বাক্য",
            iconName = "Anchor",
            systemInstructionEn = "You are a retention expert. Craft 5 high-converting opening hooks for the first 5 seconds of a video.",
            systemInstructionBn = "আপনি ওয়াচ-টাইম বৃদ্ধিকারক। ভিডিওর শুরুতে দর্শককে ধরে রাখার জন্য ৫টি অবিশ্বাস্য ওপেনিং হুক দিন।",
            promptPlaceholderEn = "e.g., Video about how to invest in stock market",
            promptPlaceholderBn = "যেমন: শেয়ার বাজারে বিনিয়োগের উপায় নিয়ে ভিডিওর ওপেনিং হুক",
            promptExamplesEn = listOf("Stop making this huge mistake with your money!", "What if I told you everything you learned about coding is wrong?"),
            promptExamplesBn = listOf("যদি বলি আপনার ফোনের এই সেটিংসটি এখনই অফ না করলে বড় বিপদ হবে?", "আপনি কি জানেন কেন ৯০% মানুষ ফ্রিল্যান্সিংয়ে ব্যর্থ হয়?"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "youtube_hashtags",
            categoryId = "youtube",
            titleEn = "Hashtags",
            titleBn = "হ্যাশট্যাগ",
            descriptionEn = "Relevant viral hashtags for Shorts and long-form videos",
            descriptionBn = "শর্টস ও বড় ভিডিও ভাইরাল করার প্রাসঙ্গিক হ্যাশট্যাগ",
            iconName = "Numbers",
            systemInstructionEn = "You are a social hashtag expert. Generate niche-relevant, high-traffic hashtags for YouTube Shorts and videos.",
            systemInstructionBn = "আপনি হ্যাশট্যাগ বিশেষজ্ঞ। ভিডিও সার্চে শীর্ষে নিয়ে যাওয়ার মতো ট্রেন্ডিং হ্যাশট্যাগ প্রদান করুন।",
            promptPlaceholderEn = "e.g., Fitness and home workout channel",
            promptPlaceholderBn = "যেমন: ফিটনেস ও জিম ওয়ার্কআউট ভিডিও হ্যাশট্যাগ",
            promptExamplesEn = listOf("Artificial Intelligence tech news", "Bangladeshi street food vlog"),
            promptExamplesBn = listOf("টেকনোলজি ও গ্যাজেট রিভিউ হ্যাশট্যাগ", "ভ্রমণ ও ট্রাভেল ব্লগিং হ্যাশট্যাগ"),
            tags = listOf("newest")
        ))
        add(AiTool(
            id = "youtube_voiceover",
            categoryId = "youtube",
            titleEn = "Voiceover Script",
            titleBn = "ভয়েসওভার স্ক্রিপ্ট",
            descriptionEn = "Clear narration scripts for faceless channel videos",
            descriptionBn = "ফেসলেস ইউটিউব চ্যানেলের জন্য ভয়েসওভার স্ক্রিপ্ট",
            iconName = "Mic",
            systemInstructionEn = "You are a narration artist. Write natural, rhythmic voiceover scripts with emotion markers and pacing notes.",
            systemInstructionBn = "আপনি ভয়েসওভার রচয়িতা। কন্ঠ দেওয়ার সুবিধার জন্য পেসিং ও আবেগ নির্দেশক সহ সুন্দর স্ক্রিপ্ট বানান।",
            promptPlaceholderEn = "e.g., Documentary about the construction of the Pyramids",
            promptPlaceholderBn = "যেমন: মিসরের পিরামিড তৈরির রহস্য নিয়ে ডকুমেন্টারি ভয়েসওভার",
            promptExamplesEn = listOf("The mysterious story of Bermuda Triangle", "The history of Internet invention"),
            promptExamplesBn = listOf("টাইটানিক জাহাজের না জানা ইতিহাস ভয়েসওভার", "মানুষ কীভাবে চাঁদে পা দিয়েছিল গল্পের ভয়েসওভার"),
            tags = listOf("trending")
        ))

        // --- CATEGORY 4: BUSINESS (10 Tools) ---
        add(AiTool(
            id = "business_name",
            categoryId = "business",
            titleEn = "Business Name",
            titleBn = "ব্যবসার নাম",
            descriptionEn = "Catchy, brandable business & startup name ideas",
            descriptionBn = "ব্যবসা বা স্টার্টআপের জন্য আধুনিক ও আকর্ষণীয় নাম",
            iconName = "Store",
            systemInstructionEn = "You are a brand naming strategist. Suggest catchy, modern, domain-available style brand names categorized by style.",
            systemInstructionBn = "আপনি ব্র্যান্ড নেমিং বিশেষজ্ঞ। স্টার্টআপের জন্য অর্থপূর্ণ, মেমোরেবল ও আধুনিক ব্র্যান্ড নাম প্রস্তাব করুন।",
            promptPlaceholderEn = "e.g., Organic honey & natural food e-commerce shop",
            promptPlaceholderBn = "যেমন: অর্গানিক ফ্রুটস ও ফুড ডেলিভারি ব্যবসার নাম",
            promptExamplesEn = listOf("AI powered travel planner app", "Premium leather shoes brand"),
            promptExamplesBn = listOf("অনলাইন ক্লদিং ও ফ্যাশন ব্র্যান্ডের নাম", "আইটি ট্রেনিং ইনস্টিটিউটের জন্য নাম"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "business_slogan",
            categoryId = "business",
            titleEn = "Slogan",
            titleBn = "স্লোগান",
            descriptionEn = "Memorable taglines and slogans for your brand",
            descriptionBn = "আপনার ব্র্যান্ডের জন্য মনে রাখার মতো ট্যাগলাইন ও স্লোগান",
            iconName = "Campaign",
            systemInstructionEn = "You are a branding slogan copywriter. Generate memorable, inspiring, and unique brand taglines.",
            systemInstructionBn = "আপনি স্লোগান মেকার। ব্রান্ডের লক্ষ্য ও মূল্যবোধ প্রকাশ করে এমন অনুপ্রেরণাদায়ক স্লোগান লিখুন।",
            promptPlaceholderEn = "e.g., Eco-friendly bamboo products startup",
            promptPlaceholderBn = "যেমন: পরিবেশবান্ধব বাঁশ ও পচনশীল পণ্যের ব্র্যান্ড স্লোগান",
            promptExamplesEn = listOf("24/7 fast courier delivery service", "Freshly roasted specialty coffee house"),
            promptExamplesBn = listOf("অনলাইন গ্রোসারি ডেলিভারি সার্ভিসের স্লোগান", "সফটওয়্যার সলিউশন কোম্পানির ট্যাগলাইন"),
            tags = listOf("trending")
        ))
        add(AiTool(
            id = "business_copy",
            categoryId = "business",
            titleEn = "Marketing Copy",
            titleBn = "মার্কেটিং কপি",
            descriptionEn = "High-converting AIDA framework marketing copy",
            descriptionBn = "পণ্য বিক্রয় বাড়ানোর কার্যকর মার্কেটিং টেক্সট (AIDA)",
            iconName = "RecordVoiceOver",
            systemInstructionEn = "You are a direct-response copywriter. Craft high-converting sales messaging using AIDA (Attention, Interest, Desire, Action).",
            systemInstructionBn = "আপনি সেলস কপিরাইটার। AIDA মডেল ব্যবহার করে গ্রাহককে আকৃষ্ট করার কন্টেন্ট তৈরি করুন।",
            promptPlaceholderEn = "e.g., Launching a new online Spoken English course",
            promptPlaceholderBn = "<ctrl42>যেমন: নতুন স্পোকেন ইংলিশ কোর্সে ভর্তি চলছ - প্রমোশনাল পোস্ট",
            promptExamplesEn = listOf("Selling ergonomic standing desk", "SaaS project management tool subscription"),
            promptExamplesBn = listOf("নতুন মডেলের স্মার্টওয়াচ বিক্রির বিজ্ঞাপন কপি", "রেস্তোরাঁর বুফে অফারের মার্কেটিং পোস্ট"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "business_fbads",
            categoryId = "business",
            titleEn = "Facebook Ads",
            titleBn = "ফেসবুক এডস",
            descriptionEn = "High-CTR Facebook ad headlines, primary text & CTA",
            descriptionBn = "ফেসবুক এডসের আকর্ষণীয় হেডলাইন, মেইন টেক্সট ও সিটিএ",
            iconName = "AdsClick",
            systemInstructionEn = "You are a Facebook Ads expert. Generate Primary Text, Headline, and Call to Action options optimized for Facebook Ads conversions.",
            systemInstructionBn = "আপনি ফেসবুক এডস এক্সপার্ট। ফেসবুকে সেল বাড়ানোর উপযোগী হেডলাইন, অফার টেক্সট ও বোতাম লিখুন।",
            promptPlaceholderEn = "e.g., Premium Panjabi collection for Eid festival",
            promptPlaceholderBn = "যেমন: ঈদের প্রিমিয়াম পাঞ্জাবি কালেকশনের ফেসবুক এড টেক্সট",
            promptExamplesEn = listOf("Facebook ad for Digital Marketing agency services", "Ad for organic skincare cream"),
            promptExamplesBn = listOf("লেদার জুতা বিক্রির জন্য ফেসবুক এডস টেক্সট", "রেডিমেড থ্রি-পিস কালেকশন ফেসবুক বিজ্ঞাপন"),
            tags = listOf("popular", "trending")
        ))
        add(AiTool(
            id = "business_igads",
            categoryId = "business",
            titleEn = "Instagram Ads",
            titleBn = "ইনস্টাগ্রাম এডস",
            descriptionEn = "Aesthetic, concise ad captions for Instagram",
            descriptionBn = "ইনস্টাগ্রামের জন্য স্টাইলিশ ও আকর্ষনীয় এড ক্যাপশন",
            iconName = "PhotoCamera",
            systemInstructionEn = "You are an Instagram Ads specialist. Craft concise, visual, lifestyle-oriented ad copy with clear swipe-up CTAs.",
            systemInstructionBn = "আপনি ইনস্টাগ্রাম এডস ডিজাইনার। লাইফস্টাইল ভ্যালু ও প্রিমিয়াম ফিল ফুটিয়ে তুলে এড ক্যাপশন লিখুন।",
            promptPlaceholderEn = "e.g., Minimalist aesthetic jewelry brand sale",
            promptPlaceholderBn = "যেমন: কাস্টমাইজড জুয়েলারি ব্র্যান্ডের ইনস্টাগ্রাম এডস",
            promptExamplesEn = listOf("Trendy streetwear hoodies store", "Luxury hotel weekend getaway package"),
            promptExamplesBn = listOf("ট্রেন্ডি সানগ্লাস কালেকশনের ইনস্টাগ্রাম বিজ্ঞাপন", "ক্যাফে ও কফি শপ প্রমোশন ক্যাপশন"),
            tags = listOf("newest")
        ))
        add(AiTool(
            id = "business_proposal",
            categoryId = "business",
            titleEn = "Proposal Writer",
            titleBn = "প্রপোজাল রাইটার",
            descriptionEn = "Professional business & project proposal templates",
            descriptionBn = "ক্লায়েন্টকে পাঠানোর জন্য প্রফেশনাল বিজনেস প্রপোজাল",
            iconName = "Assignment",
            systemInstructionEn = "You are a corporate proposal writer. Draft professional business proposals with scope of work, deliverables, timelines, and terms.",
            systemInstructionBn = "আপনি বিজনেস রাইটার। প্রজেক্টের কাজের পরিধি, ডেলিভারি টাইম ও শর্তাবলি সহ প্রপোজাল প্রস্তুত করুন।",
            promptPlaceholderEn = "e.g., Web redesign proposal for a local restaurant chain",
            promptPlaceholderBn = "যেমন: ক্লায়েন্টের জন্য সোশ্যাল মিডিয়া ম্যানেজমেন্ট প্রপোজাল",
            promptExamplesEn = listOf("Mobile app development proposal for healthcare startup", "SEO optimization proposal for ecommerce site"),
            promptExamplesBn = listOf("রেস্তোরাঁর জন্য সফটওয়্যার ডেভেলপমেন্ট প্রপোজাল", "ডিজিটাল মার্কেটিং সার্ভিস প্রপোজাল"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "business_invoice",
            categoryId = "business",
            titleEn = "Invoice Text",
            titleBn = "ইনভয়েস টেক্সট",
            descriptionEn = "Standard terms, payment instructions & invoice notes",
            descriptionBn = "ইনভয়েসের পেমেন্ট নির্দেশিকা, শর্তাদি ও থ্যাঙ্ক ইউ নোট",
            iconName = "ReceiptLong",
            systemInstructionEn = "You are a billing manager. Generate clear invoice notes, payment terms, late payment policy, and professional client thanks.",
            systemInstructionBn = "আপনি হিসাবরক্ষণ কর্মকর্তা। ইনভয়েসে লেখার উপযোগী পেমেন্ট শর্তাবলি ও ধন্যবাদ জ্ঞাপন টেক্সট লিখুন।",
            promptPlaceholderEn = "e.g., Invoice note for $500 Web Design milestone 1",
            promptPlaceholderBn = "যেমন: গ্রাফিক্স ডিজাইন প্রজেক্টের ইনভয়েস নোট ও বিকাশ/ব্যাংক পেমেন্ট ডিটেইলস",
            promptExamplesEn = listOf("Invoice terms for 50% upfront deposit", "Late payment policy warning text"),
            promptExamplesBn = listOf("ফ্রিল্যান্সিং প্রজেক্ট শেষ করে ক্লায়েন্টকে পাঠানোর বিল নোট", "ইনভয়েস পাওয়ার ৭ দিনের মধ্যে পেমেন্টের বার্তা"),
            tags = listOf("newest")
        ))
        add(AiTool(
            id = "business_salesemail",
            categoryId = "business",
            titleEn = "Sales Email",
            titleBn = "সেলস ইমেইল",
            descriptionEn = "Cold outreach and sales pitch emails that get replies",
            descriptionBn = "নতুন ক্লায়েন্ট পাওয়ার কোল্ড ইমেইল ও সেলস পিচ",
            iconName = "MarkEmailRead",
            systemInstructionEn = "You are a B2B sales expert. Write personalized, compelling cold sales outreach emails with strong value proposition.",
            systemInstructionBn = "আপনি বিটুবি সেলস স্পেশালিস্ট। সম্ভাবনা গ্রাহকের মনোযোগ কাড়তে কোল্ড সেলস ইমেইল টেমপ্লেট লিখুন।",
            promptPlaceholderEn = "e.g., Pitching SEO services to e-commerce store owners",
            promptPlaceholderBn = "যেমন: ই-কমার্স ক্লায়েন্টকে ওয়েব ডিজাইন সার্ভিসের অফার দিয়ে ইমেইল",
            promptExamplesEn = listOf("Pitching SaaS HR software to company HR managers", "Offering video editing services to YouTubers"),
            promptExamplesBn = listOf("কোম্পানির আইটি সিকিউরিটি সলিউশন বিক্রির জন্য ইমেইল", "ফটোগ্রাফি সার্ভিস বুকিং অফারের সেলস ইমেইল"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "business_faq",
            categoryId = "business",
            titleEn = "FAQ Generator",
            titleBn = "FAQ জেনারেটর",
            descriptionEn = "Frequently Asked Questions & Answers for websites",
            descriptionBn = "ওয়েবসাইটের সাধারণ জিজ্ঞাসা (FAQ) ও সঠিক উত্তর",
            iconName = "HelpOutline",
            systemInstructionEn = "You are a customer experience consultant. Generate common Frequently Asked Questions (FAQs) and reassuring answers for any business.",
            systemInstructionBn = "আপনি কাস্টমার এক্সপেরিয়েন্স স্পেশালিস্ট। ওয়েবসাইটের জন্য সম্ভাব্য প্রশ্নাবলী ও সহজ উত্তর প্রস্তুত করুন।",
            promptPlaceholderEn = "e.g., Online clothing store shipping & return policy",
            promptPlaceholderBn = "যেমন: অনলাইন ফ্যাশন শপের পেমেন্ট, ডেলিভারি ও রিটার্ন পলিসি FAQ",
            promptExamplesEn = listOf("SaaS software free trial & billing FAQ", "Dental clinic appointment booking FAQ"),
            promptExamplesBn = listOf("অনলাইন কোর্সের ভর্তি ও সার্টিফিকেট সম্পর্কিত সাধারণ প্রশ্ন উত্তর", "ট্যুর প্যাকেজ বুকিং এর FAQ"),
            tags = listOf("newest")
        ))
        add(AiTool(
            id = "business_reply",
            categoryId = "business",
            titleEn = "Customer Reply",
            titleBn = "গ্রাহক উত্তর",
            descriptionEn = "Polite replies to customer inquiries, complaints & reviews",
            descriptionBn = "গ্রাহকের জিজ্ঞাসা, অভিযোগ বা রিভিউ এর প্রফেশনাল উত্তর",
            iconName = "QuestionAnswer",
            systemInstructionEn = "You are a customer support agent. Craft empathetic, professional replies to customer complaints, reviews, or queries.",
            systemInstructionBn = "আপনি কাস্টমার সাপোর্ট ডিরেক্টর। ক্ষুব্ধ বা অনুযোগকারী গ্রাহককে শান্ত করতে মার্জিত ও প্রফেশনাল উত্তর লিখুন।",
            promptPlaceholderEn = "e.g., Customer complaining about late delivery of food order",
            promptPlaceholderBn = "যেমন: পণ্য পৌঁছাতে দেরি হওয়ায় কাস্টমারের অভিযোগের ভদ্র উত্তর",
            promptExamplesEn = listOf("Responding to 1-star negative Google review", "Replying to inquiry about product warranty"),
            promptExamplesBn = listOf("পণ্য ক্ষতিগ্রস্ত অবস্থায় পাওয়ার জন্য ক্ষমা চেয়ে ইমেইল উত্তর", "দামের বিষয়ে ডিসকাউন্ট চাইলে কীভাবে উত্তর দেবেন"),
            tags = listOf("popular")
        ))

        // --- CATEGORY 5: PROGRAMMING (10 Tools) ---
        add(AiTool(
            id = "programming_code",
            categoryId = "programming",
            titleEn = "Code Generator",
            titleBn = "কোড জেনারেটর",
            descriptionEn = "Generate clean code snippet in any programming language",
            descriptionBn = "যেকোনো প্রোগ্রামিং ভাষায় নিখুঁত কোড জেনারেট করুন",
            iconName = "Code",
            systemInstructionEn = "You are an expert software engineer. Write well-commented, robust, bug-free code snippets in requested language.",
            systemInstructionBn = "আপনি অভিজ্ঞ সফটওয়্যার ইঞ্জিনিয়ার। স্পষ্ট কমেন্ট সহ কার্যকারী কোড জেনারেট করুন।",
            promptPlaceholderEn = "e.g., Python function to scrape table data from web page",
            promptPlaceholderBn = "যেমন: পাইথনে ছবি ডাউনলোড করার কোড লিখে দাও",
            promptExamplesEn = listOf("Kotlin function to convert JSON to data class", "JavaScript function to debouncing search input"),
            promptExamplesBn = listOf("জাভাস্ক্রিপ্ট দিয়ে ফর্ম ভ্যালিডেশনের কোড", "পাইথনে টেক্সট ফাইল পড়ার সিম্পল স্ক্রিপ্ট"),
            tags = listOf("popular", "trending")
        ))
        add(AiTool(
            id = "programming_debug",
            categoryId = "programming",
            titleEn = "Debug Code",
            titleBn = "ডিবাগ কোড",
            descriptionEn = "Find bugs, memory leaks, logic errors & fix them",
            descriptionBn = "কোডের ভুল, বাগ বা এরর খুঁজে বের করে সমাধান করুন",
            iconName = "BugReport",
            systemInstructionEn = "You are a master code debugger. Identify errors, syntax bugs, memory leaks, and performance issues in code and provide corrected version.",
            systemInstructionBn = "আপনি কোড ডিবাগার। প্রদত্ত কোডের সমস্যা ও ভুল সনাক্ত করে সঠিক কোড ও সমাধান বুঝিয়ে দিন।",
            promptPlaceholderEn = "Paste your broken code and error message here...",
            promptPlaceholderBn = "এখানে আপনার সমস্যাযুক্ত কোড ও এরর মেসেজ দিন...",
            promptExamplesEn = listOf("TypeError: Cannot read properties of undefined (reading 'map')", "NullPointerException in Kotlin AsyncTask execution"),
            promptExamplesBn = listOf("পাইথনে IndentationError: unexpected indent সমাধান", "জাভাস্ক্রিপ্টে Uncaught ReferenceError সমাধান"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "programming_explain",
            categoryId = "programming",
            titleEn = "Explain Code",
            titleBn = "কোড ব্যাখ্যা",
            descriptionEn = "Line-by-line breakdown and logic explanation of code",
            descriptionBn = "কঠিন কোডের প্রতি লাইনের ব্যাখ্যা সহজ বাংলায় বুঝুন",
            iconName = "FindInPage",
            systemInstructionEn = "You are a computer science educator. Explain code snippets line by line, defining variables, loops, and underlying architecture.",
            systemInstructionBn = "আপনি প্রোগ্রামিং মেন্টর। যেকোনো অ্যালগরিদম বা কোড ব্লকের কাজ সহজ ভাষায় লাইন বাই লাইন বুঝিয়ে বলুন।",
            promptPlaceholderEn = "Paste code snippet to explain...",
            promptPlaceholderBn = "বুঝতে চাওয়া কোডটি এখানে পেস্ট করুন...",
            promptExamplesEn = listOf("Explain Dijkstra's shortest path algorithm in C++", "Explain React useEffect hook with dependency array"),
            promptExamplesBn = listOf("পাইথনে রিকার্সন (Recursion) এর কোড ব্যাখ্যা কর", "জাভাস্ক্রিপ্ট Promis এবং Async/Await কোড ব্যাখ্যা"),
            tags = listOf("trending")
        ))
        add(AiTool(
            id = "programming_html",
            categoryId = "programming",
            titleEn = "HTML Builder",
            titleBn = "এইচটিএমএল বিল্ডার",
            descriptionEn = "Clean HTML5 layout structure, forms, tables & components",
            descriptionBn = "এইচটিএমএল৫ এর স্ট্যান্ডার্ড লেআউট, ফর্ম ও টেবিল বানান",
            iconName = "Html",
            systemInstructionEn = "You are a frontend developer. Generate semantic, accessible, well-indented HTML5 structures.",
            systemInstructionBn = "আপনি ফ্রন্টএন্ড ডেভেলপার। অ্যাক্সেসিবল ও স্ট্যান্ডার্ড এইচটিএমএল স্ট্রাকচার কোড লিখুন।",
            promptPlaceholderEn = "e.g., Responsive contact form with name, email, subject and message",
            promptPlaceholderBn = "যেমন: সুন্দর একটি রেসপনসিভ লগইন ফর্মের এইচটিএমএল কোড",
            promptExamplesEn = listOf("Pricing table HTML layout with 3 tiers", "E-commerce product detail HTML section"),
            promptExamplesBn = listOf("ওয়েবসাইটের ফুটার সেকশনের এইচটিএমএল কোড", "নিউজলেটার সাবস্ক্রিপশন ফর্ম এইচটিএমএল"),
            tags = listOf("newest")
        ))
        add(AiTool(
            id = "programming_css",
            categoryId = "programming",
            titleEn = "CSS Generator",
            titleBn = "সিএসএস জেনারেটর",
            descriptionEn = "Flexbox, Grid, CSS Glassmorphism & Keyframe Animations",
            descriptionBn = "ফ্লেক্সবক্স, গ্রিড, গ্লাসমর্ফিজম ও সিএসএস এনিমেশন",
            iconName = "Css",
            systemInstructionEn = "You are a UI/CSS magician. Generate clean CSS3 styling, Flexbox/Grid layouts, custom variables, and keyframe animations.",
            systemInstructionBn = "আপনি সিএসএস বিশেষজ্ঞ। আধুনিক ওয়েবসাইট স্টাইলিং, ফ্লেক্সবক্স, ব্যাকগ্রাউন্ড গ্রাডিয়েন্ট ও এনিমেশন কোড তৈরি করুন।",
            promptPlaceholderEn = "e.g., Glassmorphism card effect with blur and subtle gradient border",
            promptPlaceholderBn = "যেমন: সুন্দর একটি বাটন হোভার এনিমেশন সিএসএস কোড",
            promptExamplesEn = listOf("3 column responsive CSS Grid layout", "Pulsing glowing button animation CSS"),
            promptExamplesBn = listOf("ডার্ক মোড কার্ড লেআউটের সিএসএস স্টাইল", "সার্কেল লোডিং স্পিনারের সিএসএস কোড"),
            tags = listOf("trending")
        ))
        add(AiTool(
            id = "programming_js",
            categoryId = "programming",
            titleEn = "JavaScript Helper",
            titleBn = "জাভাস্ক্রিপ্ট হেলপার",
            descriptionEn = "DOM manipulation, Fetch API, Async/Await & JS utilities",
            descriptionBn = "ডম ম্যানিপুলেশন, ফেচ এপিআই ও জাভাস্ক্রিপ্ট ফাংশন",
            iconName = "Javascript",
            systemInstructionEn = "You are a JavaScript developer. Write ES6+ modern JavaScript functions for DOM manipulation, events, array methods, and API calls.",
            systemInstructionBn = "আপনি জাভাস্ক্রিপ্ট স্পেশালিস্ট। আধুনিক ES6+ সিনট্যাক্সে জাভাস্ক্রিপ্ট ফাংশন ও এপিআই কল কোড তৈরি করুন।",
            promptPlaceholderEn = "e.g., Fetch data from API and render list dynamically in DOM",
            promptPlaceholderBn = "যেমন: ফিল্টার সহ সার্চ বার বানানোর জাভাস্ক্রিপ্ট কোড",
            promptExamplesEn = listOf("Filter array of objects by search term and render", "LocalStorage item save and retrieve function"),
            promptExamplesBn = listOf("বাটনে ক্লিক করলে ডার্ক মোড টগল করার জাভাস্ক্রিপ্ট", "কাউন্টডাউন টাইমার তৈরির জাভাস্ক্রিপ্ট স্ক্রিপ্ট"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "programming_python",
            categoryId = "programming",
            titleEn = "Python Helper",
            titleBn = "পাইথন হেলপার",
            descriptionEn = "Automation scripts, Pandas, Data Analysis & Django/Flask",
            descriptionBn = "অটোমেশন স্ক্রিপ্ট, ডাটা প্রসেসিং ও পাইথন টিপস",
            iconName = "Terminal",
            systemInstructionEn = "You are a Python expert. Write Python scripts for data analysis, automation, web scraping, and API integration.",
            systemInstructionBn = "আপনি পাইথন ডেভেলপার। পাইথন দিয়ে কাজ অটোমেশন, ডাটা এনালাইসিস বা প্রজেক্ট কোড প্রস্তুত করুন।",
            promptPlaceholderEn = "e.g., Script to convert PDF pages into PNG images",
            promptPlaceholderBn = "যেমন: এক্সেল ফাইল থেকে নির্দিষ্ট তথ্য ফিল্টার করার পাইথন স্ক্রিপ্ট",
            promptExamplesEn = listOf("Download all images from URL using Python requests and BeautifulSoup", "Calculate average and standard deviation with Pandas"),
            promptExamplesBn = listOf("পাইথনে একসাথে একাধিক টেক্সট ফাইলের নাম পরিবর্তন স্ক্রিপ্ট", "ওয়েব স্ক্র্যাপিং এর মাধ্যমে খবরের শিরোনাম নামানোর কোড"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "programming_sql",
            categoryId = "programming",
            titleEn = "SQL Helper",
            titleBn = "এসকিউএল হেলপার",
            descriptionEn = "Complex SQL queries, JOINs, Indexing & database schema",
            descriptionBn = "ডাটাবেজের জটিল SQL কোয়েরি, JOIN ও স্কিমা তৈরি",
            iconName = "Storage",
            systemInstructionEn = "You are a Database Administrator. Write optimized SQL queries, database migrations, schema definitions, and joins.",
            systemInstructionBn = "আপনি ডাটাবেজ এ্যাডমিনিস্ট্রেটর। ডাটাবেজ টেবিল স্কিমা ও জটিল SQL কোয়েরি তৈরি করে দিন।",
            promptPlaceholderEn = "e.g., Get top 5 customers with highest total order amount in 2025",
            promptPlaceholderBn = "যেমন: দুটি টেবিল থেকে JOIN করে তথ্য বের করার SQL Query",
            promptExamplesEn = listOf("SQL query to find duplicate emails in users table", "Create table query with primary key and foreign key constraints"),
            promptExamplesBn = listOf("ইউজার ও অর্ডারস টেবিল থেকে সবচেয়ে বেশি কেনাকাটা করা কাস্টমারের তালিকা", "ডাটাবেজে নতুন কলাম যোগ ও ইনডেক্সিং করার এসকিউএল"),
            tags = listOf("newest")
        ))
        add(AiTool(
            id = "programming_regex",
            categoryId = "programming",
            titleEn = "Regex Generator",
            titleBn = "রেজেক্স জেনারেটর",
            descriptionEn = "Regular Expression patterns for validation & text extraction",
            descriptionBn = "ইনপুট ভ্যালিডেশন ও প্যাটার্ন ম্যাচের Regular Expression",
            iconName = "Pattern",
            systemInstructionEn = "You are a Regex master. Provide accurate Regular Expression patterns with clear breakdowns of expression tokens.",
            systemInstructionBn = "আপনি রেজেক্স এক্সপার্ট। ইমেইল, ফোন নম্বর বা প্যাটার্ন মেলানোর জন্য সঠিক Regular Expression ও তার ব্যাখ্যা দিন।",
            promptPlaceholderEn = "e.g., Regex to validate Bangladeshi phone numbers (+8801...)",
            promptPlaceholderBn = "যেমন: বাংলাদেশী মোবাইল নম্বর ভ্যালিডেশনের রেজেক্স প্যাটার্ন",
            promptExamplesEn = listOf("Regex for strong password (8+ chars, uppercase, number, symbol)", "Regex to extract URL links from long paragraph"),
            promptExamplesBn = listOf("সঠিক ইমেইল এড্রেস ভ্যালিডেশনের রেজেক্স", "তারিখ (DD/MM/YYYY) ফরম্যাট চেক করার Regex"),
            tags = listOf("newest")
        ))
        add(AiTool(
            id = "programming_apidoc",
            categoryId = "programming",
            titleEn = "API Documentation",
            titleBn = "এপিআই ডকুমেন্টেশন",
            descriptionEn = "REST API documentation with request, response & status codes",
            descriptionBn = "রেস্ট এপিআই এন্ডপয়েন্ট ও রেসপন্স ডকুমেন্টেশন",
            iconName = "Api",
            systemInstructionEn = "You are a Technical Writer. Generate clean, OpenAPI/Swagger style API documentation for endpoints including headers, body, and responses.",
            systemInstructionBn = "আপনি টেকনিক্যাল রাইটার। মেথড, হেডার্স, রিকোয়েস্ট বডি ও রেসপন্স কোড সহ সুন্দর এপিআই ডক লিখুন।",
            promptPlaceholderEn = "e.g., POST /api/v1/auth/login endpoint documentation",
            promptPlaceholderBn = "যেমন: ইউজার রেজিস্ট্রেশন এপিআই (POST /api/register) এর জন্য ডকুমেন্টেশন",
            promptExamplesEn = listOf("GET /api/v1/products with pagination query params", "PUT /api/v1/users/:id profile update API doc"),
            promptExamplesBn = listOf("প্রোডাক্ট লিস্ট পাওয়ার GET API ডকুমেন্টেশন", "পাসওয়ার্ড রিসেট এপিআই এন্ডপয়েন্টের ডকুমেন্টেশন"),
            tags = listOf("trending")
        ))

        // --- CATEGORY 6: SOCIAL MEDIA (10 Tools) ---
        add(AiTool(
            id = "social_fbpost",
            categoryId = "social",
            titleEn = "Facebook Post",
            titleBn = "ফেসবুক পোস্ট",
            descriptionEn = "Engaging Facebook status posts that drive comments & shares",
            descriptionBn = "আকর্ষণীয় ফেসবুক পোস্ট যা লাইক, কমেন্ট ও শেয়ার বাড়ায়",
            iconName = "Facebook",
            systemInstructionEn = "You are a Facebook social growth manager. Write highly engaging, discussion-provoking Facebook posts.",
            systemInstructionBn = "আপনি ফেসবুক কন্টেন্ট ক্রিয়েটর। পাঠকদের যুক্ত করার উপযোগী মতামতধর্মী ও শেয়ার করার মতো পোস্ট লিখুন।",
            promptPlaceholderEn = "e.g., Thought-provoking post about balancing work and personal life",
            promptPlaceholderBn = "যেমন: বই পড়ার গুরুত্ব নিয়ে একটি সুন্দর আবেগপূর্ণ ফেসবুক পোস্ট",
            promptExamplesEn = listOf("Personal achievement update post", "Discussion question post about remote work vs office"),
            promptExamplesBn = listOf("নতুন বছর শুরু নিয়ে অনুপ্রেরণামূলক ফেসবুক পোস্ট", "বৃষ্টির দিনে চায়ের কাপে আড্ডার ফেসবুক স্ট্যাটাস"),
            tags = listOf("popular", "trending")
        ))
        add(AiTool(
            id = "social_igcaption",
            categoryId = "social",
            titleEn = "Instagram Caption",
            titleBn = "ইনস্টাগ্রাম ক্যাপশন",
            descriptionEn = "Aesthetic, trendy captions with line breaks & emojis",
            descriptionBn = "ইনস্টাগ্রামের জন্য স্টাইলিশ ক্যাপশন ও ট্রেন্ডিং হ্যাশট্যাগ",
            iconName = "CameraAlt",
            systemInstructionEn = "You are an Instagram aesthetic creator. Write visually clean, witty, or inspirational captions with strategic emojis.",
            systemInstructionBn = "আপনি ইনস্টাগ্রাম এক্সপার্ট। ছবির সাথে মানানসই নান্দনিক ও ট্রেন্ডি ক্যাপশন তৈরি করুন।",
            promptPlaceholderEn = "e.g., Photo wearing ethnic traditional outfit at wedding",
            promptPlaceholderBn = "যেমন: বিয়েবাড়িতে ঐতিহ্যবাহী পাঞ্জাবি বা শাড়ি পরা ছবির ক্যাপশন",
            promptExamplesEn = listOf("Gym selfie photo caption", "Travel memories in the mountains photo caption"),
            promptExamplesBn = listOf("ভ্রমণের রিলস এর জন্য সুন্দর ক্যাপশন", "ক্যাফেতে কফি খাওয়ার পিকচারের ক্যাপশন"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "social_linkedin",
            categoryId = "social",
            titleEn = "LinkedIn Post",
            titleBn = "লিঙ্কডইন পোস্ট",
            descriptionEn = "Thought leadership & career milestone posts for LinkedIn",
            descriptionBn = "ক্যারিয়ার, অভিজ্ঞতা ও পেশাদার সাফল্যের লিঙ্কডইন পোস্ট",
            iconName = "WorkHistory",
            systemInstructionEn = "You are a personal branding consultant. Write professional, insightful LinkedIn posts with hooks, short paragraphs, and key lessons.",
            systemInstructionBn = "আপনি পার্সোনাল ব্র্যান্ডিং পরামর্শক। লিঙ্কডইনে প্রফেশনাল ভ্যালু যোগ করার মতো গল্প ও অভিজ্ঞতা সম্পর্কিত পোস্ট লিখুন।",
            promptPlaceholderEn = "e.g., Lessons learned after completing my first year as Product Manager",
            promptPlaceholderBn = "যেমন: প্রথম প্রজেক্ট সফলভাবে শেষ করার পর লিঙ্কডইন পোস্ট",
            promptExamplesEn = listOf("5 career advice tips I wish I knew at 22", "How our team scaled our software user base"),
            promptExamplesBn = listOf("নতুন চাকরিতে যোগদানের অনুভূতি জানিয়ে লিঙ্কডইন পোস্ট", "ব্যর্থতা থেকে শিক্ষা পাওয়ার পেশাদার গল্প"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "social_twitter",
            categoryId = "social",
            titleEn = "Twitter Post",
            titleBn = "টুইটার পোস্ট",
            descriptionEn = "280-character tweets or multi-tweet thread outlines",
            descriptionBn = "২৮০ অক্ষরের ভাইরাল টুইট বা পরপর টুইটার থ্রেড",
            iconName = "Tag",
            systemInstructionEn = "You are a viral Twitter strategist. Write punchy 280-character tweets or informative Twitter/X threads.",
            systemInstructionBn = "আপনি টুইটার কন্টেন্ট রাইটার। সংক্ষিপ্ত, তীক্ষ্ণ ও ভাইরাল হওয়ার মতো টুইট বা থ্রেড তৈরি করুন।",
            promptPlaceholderEn = "e.g., Thread on 5 free AI tools every student must use",
            promptPlaceholderBn = "যেমন: ৫টি সেরা ওয়েবসাইটের তালিকা নিয়ে টুইটার থ্রেড",
            promptExamplesEn = listOf("Tweet about productivity hacks", "5 part Twitter thread on building in public"),
            promptExamplesBn = listOf("প্রোগ্রামিং শেখার ৫টি সহজ ট্রিক নিয়ে টুইট", "দৈনন্দিন কাজের সময় বাঁচানোর কায়দা নিয়ে টুইট"),
            tags = listOf("trending")
        ))
        add(AiTool(
            id = "social_bio",
            categoryId = "social",
            titleEn = "Bio Generator",
            titleBn = "বায়ো জেনারেটর",
            descriptionEn = "Impressionable social media bio for Instagram, Twitter or LinkedIn",
            descriptionBn = "ইনস্টাগ্রাম, ফেসবুক বা লিঙ্কডইনের জন্য আকর্ষণীয় বায়ো",
            iconName = "AccountCircle",
            systemInstructionEn = "You are a social bio designer. Craft concise, high-impact bios highlighting personality, profession, and call-to-action.",
            systemInstructionBn = "আপনি সোশ্যাল প্রোফাইল এক্সপার্ট। ব্যক্তিত্ব ও পেশা ফুটিয়ে তুলে সুন্দর বায়ো বানিয়ে দিন।",
            promptPlaceholderEn = "e.g., Freelance Web Developer & coffee lover",
            promptPlaceholderBn = "যেমন: কনটেন্ট ক্রিয়েটর, ট্রাভেলার ও টেকনোলজি লাভার",
            promptExamplesEn = listOf("Digital Entrepreneur & Fitness enthusiast", "UI/UX Designer sharing daily tips"),
            promptExamplesBn = listOf("ফটোগ্রাফার ও ভিডিও এডিটর বায়ো", "বিশ্ববিদ্যালয়ের ছাত্র ও ফ্রিল্যান্সার বায়ো"),
            tags = listOf("newest")
        ))
        add(AiTool(
            id = "social_reply",
            categoryId = "social",
            titleEn = "Comment Reply",
            titleBn = "কমেন্ট রিপ্লাই",
            descriptionEn = "Witty, polite or viral replies to post comments",
            descriptionBn = "পোস্টের কমেন্টে মজার, মার্জিত বা চতুর উত্তর দিন",
            iconName = "ChatBubbleOutline",
            systemInstructionEn = "You are a community engagement manager. Generate friendly, clever, or helpful replies to social media post comments.",
            systemInstructionBn = "আপনি সোশ্যাল মিডিয়া উত্তরদাতা। শুভাকাঙ্ক্ষী বা ফলোয়ারদের কমেন্টের সুন্দর ও হৃদয়গ্রাহী উত্তর দিন।",
            promptPlaceholderEn = "e.g., Replying to someone saying 'Awesome content, keep it up!'",
            promptPlaceholderBn = "যেমন: কেউ 'খুব সুন্দর হয়েছে আপনার ছবিটা' কমেন্ট করলে উত্তর",
            promptExamplesEn = listOf("Replying to criticism on a blog post", "Responding to congratulations on promotion"),
            promptExamplesBn = listOf("উপহার পাওয়ার ছবিতে শুভকামনা জানানোর উত্তর", "পরামর্শ চাওয়ার কমেন্টে সাহায্যকারী জবাব"),
            tags = listOf("trending")
        ))
        add(AiTool(
            id = "social_reel",
            categoryId = "social",
            titleEn = "Reel Script",
            titleBn = "রিল স্ক্রিপ্ট",
            descriptionEn = "Instagram Reels & TikTok short video concepts",
            descriptionBn = "ইনস্টাগ্রাম রিলস ও টিকটক শর্ট ভিডিও স্ক্রিপ্ট",
            iconName = "VideoLibrary",
            systemInstructionEn = "You are a TikTok/Reels director. Write trending short video concepts with music recommendations, visual cuts, and captions.",
            systemInstructionBn = "আপনি শর্ট ভিডিও ডিরেক্টর। ব্যাকগ্রাউন্ড মিউজিক আইডিয়া, ভিডিও কাট ও ডায়ালগ সহ রিলস স্ক্রিপ্ট লিখুন।",
            promptPlaceholderEn = "e.g., A day in the life of a remote software developer",
            promptPlaceholderBn = "যেমন: একজন ফ্রিল্যান্সারের সারাদিনের রুটিন নিয়ে রিলস",
            promptExamplesEn = listOf("3 Outfit transitions for college students", "Quick 30 second pasta recipe reel"),
            promptExamplesBn = listOf("পুরাতন জিনিস দিয়ে ঘর সাজানোর আইডিয়া রিলস", "৩টি বই যা আপনার জীবন বদলে দেবে রিলস"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "social_poll",
            categoryId = "social",
            titleEn = "Poll Ideas",
            titleBn = "পোল আইডিয়া",
            descriptionEn = "Interactive voting polls for Instagram Stories & Twitter",
            descriptionBn = "ইনস্টাগ্রাম স্টোরি ও ফেসবুক পোলের জন্য প্রশ্ন ও অপশন",
            iconName = "Poll",
            systemInstructionEn = "You are an engagement optimizer. Create interesting A/B or multiple choice poll questions that encourage audience interaction.",
            systemInstructionBn = "আপনি এঙ্গেজমেন্ট এক্সপার্ট। দর্শকদের ভোট দিতে উৎসাহিত করে এমন আকর্ষণীয় পোল প্রশ্ন তৈরি করুন।",
            promptPlaceholderEn = "e.g., Topic: Work From Home vs Office Work",
            promptPlaceholderBn = "যেমন: চা বনাম কফি কোনটা আপনার প্রিয়? - পোল প্রশ্ন",
            promptExamplesEn = listOf("Poll about favorite programming language", "Poll about weekend vacation destination"),
            promptExamplesBn = listOf("বই পড়ার জন্য হার্ডকপি বনাম ইবুক পোল", "ভ্রমণের জন্য সমুদ্র বনাম পাহাড় পোল"),
            tags = listOf("newest")
        ))
        add(AiTool(
            id = "social_emoji",
            categoryId = "social",
            titleEn = "Emoji Enhancer",
            titleBn = "ইমোজি এনহ্যান্সার",
            descriptionEn = "Enhance plain text with expressive visual emojis",
            descriptionBn = "যেকোনো সাধারণ লেখায় মানানসই ইমোজি যুক্ত করে সুন্দর করুন",
            iconName = "EmojiEmotions",
            systemInstructionEn = "You are an emoji stylist. Take plain text and naturally insert fitting emojis to make it visually attractive and readable.",
            systemInstructionBn = "আপনি ইমোজি ডিজাইনার। সাধারণ অনুচ্ছেদের মধ্যে মানানসই ও আকর্ষণীয় ইমোজি যুক্ত করে স্টাইলিশ করুন।",
            promptPlaceholderEn = "Paste your plain paragraph here...",
            promptPlaceholderBn = "এখানে ইমোজি ছাড়া সাধারণ লেখাটি পেস্ট করুন...",
            promptExamplesEn = listOf("Welcome to our brand new store! We have discounts on all items.", "Good morning everyone! Hope you have a productive monday."),
            promptExamplesBn = listOf("আমাদের নতুন শোরুমে আপনাকে স্বাগতম। আজকে সকল মালামালে বিশেষ ছাড়।", "শুভ সকাল বন্ধুরা! আশা করি আজকের দিনটি সবার চমৎকার কাটবে।"),
            tags = listOf("newest")
        ))
        add(AiTool(
            id = "social_trend",
            categoryId = "social",
            titleEn = "Trend Finder",
            titleBn = "ট্রেন্ড ফাইন্ডার",
            descriptionEn = "Discover viral content themes & trending topics",
            descriptionBn = "সাম্প্রতিক ট্রেন্ডিং টপিক ও সোশ্যাল মিডিয়া থিম খুঁজুন",
            iconName = "TrendingUp",
            systemInstructionEn = "You are a viral trend analyst. Identify current viral internet trends, memes, and content hooks in a given industry.",
            systemInstructionBn = "আপনি ট্রেন্ড এনালিস্ট। বর্তমানে ইন্টারনেটে চলমান ট্রেন্ড, মিমস ও ভাইরাল বিষয় নিয়ে আইডিয়া প্রকাশ করুন।",
            promptPlaceholderEn = "e.g., What's trending in Tech & AI right now?",
            promptPlaceholderBn = "যেমন: বর্তমানে তরুণদের মধ্যে কোন বিষয়গুলো ভাইরাল হচ্ছে?",
            promptExamplesEn = listOf("Trending audio formats for Instagram Reels", "Trending memes in gaming community"),
            promptExamplesBn = listOf("বর্তমানে টিকটকে কোন ধরণের কন্টেন্ট বেশি চলছে?", "ইউটিউব শর্টসে সাম্প্রতিক ভাইরাল ট্রেন্ডস"),
            tags = listOf("trending")
        ))

        // --- CATEGORY 7: IMAGE PROMPT (10 Tools) ---
        add(AiTool(
            id = "prompt_logo",
            categoryId = "image_prompt",
            titleEn = "Logo Prompt",
            titleBn = "লোগো প্রম্পট",
            descriptionEn = "Detailed AI image prompts for vector logos & badges",
            descriptionBn = "মিডজার্নি ও ডাল-ই এর জন্য প্রিমিয়াম লোগো ডিজাইন প্রম্পট",
            iconName = "Brush",
            systemInstructionEn = "You are an AI prompt engineer for Midjourney and DALL-E. Craft detailed image prompts for clean, vector vector logos.",
            systemInstructionBn = "আপনি এআই প্রম্পট ইঞ্জিনিয়ার। নিখুঁত লোগো তৈরির জন্য মিডজার্নি বা ডাল-ই প্রম্পট লিখুন।",
            promptPlaceholderEn = "e.g., Minimalist geometric fox icon for tech startup",
            promptPlaceholderBn = "যেমন: কফি শপের জন্য ভিন্টেজ ব্যাজ লোগোর প্রম্পট",
            promptExamplesEn = listOf("Cyberpunk glowing neon dragon emblem logo", "Luxury gold monoline initial letter M logo"),
            promptExamplesBn = listOf("একটি আধুনিক সফটওয়্যার কোম্পানির মিনিমালিস্ট লোগো প্রম্পট", "অর্গানিক ফুড ব্র্যান্ডের সবুজ পাতা দিয়ে তৈরি লোগো"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "prompt_anime",
            categoryId = "image_prompt",
            titleEn = "Anime Prompt",
            titleBn = "এনিমে প্রম্পট",
            descriptionEn = "Vibrant Studio Ghibli or Makoto Shinkai style anime prompts",
            descriptionBn = "স্টুডিও গিবলি বা অ্যানিমে স্টাইলের ছবি আঁকার প্রম্পট",
            iconName = "Palette",
            systemInstructionEn = "You are an anime art prompt creator. Write detailed prompts capturing anime art styles like Makoto Shinkai, Ghibli, 90s retro anime.",
            systemInstructionBn = "আপনি এনিমে আর্ট এক্সপার্ট। প্রাণবন্ত রঙ, লাইটিং ও অ্যানিমে ক্যারেক্টার আঁকার বিস্তারিত প্রম্পট লিখুন।",
            promptPlaceholderEn = "e.g., Girl looking at starry sky on a rooftop in rain",
            promptPlaceholderBn = "যেমন: বৃষ্টি ভেজা দিনে ট্রেনে জানালার পাশে বসে থাকা মেয়ের এনিমে প্রম্পট",
            promptExamplesEn = listOf("Studio Ghibli cozy green countryside cottage with flowers", "Cyberpunk anime samurai warrior in neon illuminated alley"),
            promptExamplesBn = listOf("জোছনা রাতে নদীর ঘাটে এনিমে দৃশ্য প্রম্পট", "ভবিষ্যতের মেচা রোবট এনিমে আর্ট প্রম্পট"),
            tags = listOf("popular", "trending")
        ))
        add(AiTool(
            id = "prompt_realistic",
            categoryId = "image_prompt",
            titleEn = "Realistic Prompt",
            titleBn = "রিয়েলিস্টিক প্রম্পট",
            descriptionEn = "Photorealistic 8K image prompts with camera & lighting details",
            descriptionBn = "৮কে ফটো-রিয়েলিস্টিক ছবি আঁকার ক্যামেরা ও লাইটিং প্রম্পট",
            iconName = "PhotoCamera",
            systemInstructionEn = "You are a master photographer prompt engineer. Generate photorealistic prompts specifying lens (85mm), lighting (golden hour), and render details.",
            systemInstructionBn = "আপনি আলোকচিত্র প্রম্পট ইঞ্জিনিয়ার। বাস্তবসম্মত ফটোগ্রাফির জন্য লেন্স, আলো ও ডিটেইলিং প্রম্পট লিখুন।",
            promptPlaceholderEn = "e.g., Portrait of an old craftsman working on pottery",
            promptPlaceholderBn = "যেমন: সুন্দরবনের রয়েল বেঙ্গল টাইগারের ফোটোরিয়েলিস্টিক ছবি প্রম্পট",
            promptExamplesEn = listOf("Cinematic shot of a futuristic sports car driving through desert", "Macro photograph of a dew drop on a red rose petal"),
            promptExamplesBn = listOf("পাহাড়ের চূড়ায় সূর্যাস্তের অসাধারণ ল্যান্ডস্কেপ ফটোগ্রাফি প্রম্পট", "পুরানো কটেজে ফায়ারপ্লেসের পাশে বসা বিড়ালের ছবি"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "prompt_poster",
            categoryId = "image_prompt",
            titleEn = "Poster Prompt",
            titleBn = "পোস্টার প্রম্পট",
            descriptionEn = "Movie, event and graphic design poster prompts",
            descriptionBn = "মুভি, কনসার্ট ও গ্রাফিক্স ডিজাইন পোস্টার তৈরির প্রম্পট",
            iconName = "Wallpaper",
            systemInstructionEn = "You are a graphic poster designer. Craft visual prompts for movie posters, event flyers, and artistic wall prints.",
            systemInstructionBn = "আপনি পোস্টার ডিজাইনার। সিনেমা, মিউজিক কনসার্ট বা ইভেন্টের চোখধাঁধানো পোস্টার প্রম্পট লিখুন।",
            promptPlaceholderEn = "e.g., Sci-Fi movie poster about Mars colonization",
            promptPlaceholderBn = "যেমন: কনসার্টের জন্য সাইকেডেলিক মিউজিক পোস্টার প্রম্পট",
            promptExamplesEn = listOf("Vintage 1970s jazz festival poster illustration", "Action film poster featuring dramatic shadows and explosions"),
            promptExamplesBn = listOf("বাংলাদেশি কালচারাল উৎসবের আর্ট পোস্টার প্রম্পট", "সুপারহিরো মুভির অ্যাকশন পোস্টার প্রম্পট"),
            tags = listOf("trending")
        ))
        add(AiTool(
            id = "prompt_thumbnail",
            categoryId = "image_prompt",
            titleEn = "Thumbnail Prompt",
            titleBn = "থাম্বনেইল প্রম্পট",
            descriptionEn = "High contrast, expressive face YouTube thumbnail prompts",
            descriptionBn = "ইউটিউব থাম্বনেইলের জন্য হাই-কনট্রাস্ট ও আকর্ষনীয় ছবির প্রম্পট",
            iconName = "FeaturedVideo",
            systemInstructionEn = "You are a YouTube thumbnail visual designer. Craft prompts for high-contrast, expressive, vibrant AI generated thumbnail background art.",
            systemInstructionBn = "আপনি ইউটিউব ভিজ্যুয়াল প্রম্পট মেকার। থাম্বনেইলে ব্যবহারের জন্য চোখ ধাঁধানো ব্যাকগ্রাউন্ড প্রম্পট লিখুন।",
            promptPlaceholderEn = "e.g., Man looking shocked at floating glowing Bitcoin coin",
            promptPlaceholderBn = "যেমন: একটি রহস্যময় সোনালী বাক্সের দিকে অবাক হয়ে তাকিয়ে থাকা ব্যক্তির ছবি",
            promptExamplesEn = listOf("Futuristic gamer room with neon RGB light setup", "Secret hidden treasure chest underwater with glowing rays"),
            promptExamplesBn = listOf("রোবট ও মানুষের হাত মেলানোর হাই-কনট্রাস্ট থাম্বনেইল প্রম্পট", "মহাকাশে ভাসমান রহস্যময় দরজার প্রম্পট"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "prompt_product",
            categoryId = "image_prompt",
            titleEn = "Product Prompt",
            titleBn = "প্রোডাক্ট প্রম্পট",
            descriptionEn = "Studio photoshoot prompts for e-commerce products",
            descriptionBn = "ই-কমার্স পণ্যের জন্য প্রফেশনাল স্টুডিও ফটোশুট প্রম্পট",
            iconName = "ShoppingBasket",
            systemInstructionEn = "You are a commercial product photographer. Write prompts for clean studio product displays with softbox lighting and elegant podiums.",
            systemInstructionBn = "আপনি কমার্শিয়াল ফটোগ্রাফার। পণ্যের ক্যাটালগের জন্য স্টুডিও লাইটিং ও পোডিয়াম ব্যাকগ্রাউন্ড প্রম্পট বানান।",
            promptPlaceholderEn = "e.g., Perfume bottle on a marble pedestal surrounded by water ripples",
            promptPlaceholderBn = "যেমন: মার্বেল পাথরের ওপর দামী পারফিউম বোতলের স্টুডিও ছবি প্রম্পট",
            promptExamplesEn = listOf("Sneakers floating in mid-air with splashing color powder", "Skin cream jar on smooth beige sand with palm leaves shadow"),
            promptExamplesBn = listOf("অর্গানিক ফেসওয়াশ বোতলের প্রাকৃতিক কাঠের ওপর স্টুডিও শুট", "প্রিমিয়াম ঘড়ির ডার্ক ব্যাকগ্রাউন্ড ফটোশুট প্রম্পট"),
            tags = listOf("newest")
        ))
        add(AiTool(
            id = "prompt_bg",
            categoryId = "image_prompt",
            titleEn = "Background Prompt",
            titleBn = "ব্যাকগ্রাউন্ড প্রম্পট",
            descriptionEn = "Desktop wallpapers, zoom backgrounds & texture prompts",
            descriptionBn = "ওয়ালপেপার, জুম ব্যাকগ্রাউন্ড ও ৩ডি টেক্সচার প্রম্পট",
            iconName = "CropOriginal",
            systemInstructionEn = "You are an environmental background artist. Craft prompts for stunning digital landscapes, subtle textures, and 3D abstract backdrops.",
            systemInstructionBn = "আপনি ব্যাকগ্রাউন্ড আর্টিস্ট। ডেসকটপ ওয়ালপেপার বা ডিজিটাল প্রেজেন্টেশনের জন্য নান্দনিক ব্যাকগ্রাউন্ড প্রম্পট লিখুন।",
            promptPlaceholderEn = "e.g., Abstract 3D fluid wave gradient background in dark blue and purple",
            promptPlaceholderBn = "যেমন: নীল ও বেগুনী থিমের থ্রিডি আবস্ট্রাক্ট ব্যাকগ্রাউন্ড প্রম্পট",
            promptExamplesEn = listOf("Cozy aesthetic bookshelf study room interior background", "Minimalist Scandinavian living room with large window"),
            promptExamplesBn = listOf("আধুনিক লাক্সারি অফিস রুমের জুম ব্যাকগ্রাউন্ড প্রম্পট", "পাহাড়ের চূড়ায় নক্ষত্রখচিত রাতের আকাশ ওয়ালপেপার"),
            tags = listOf("trending")
        ))
        add(AiTool(
            id = "prompt_character",
            categoryId = "image_prompt",
            titleEn = "Character Prompt",
            titleBn = "ক্যারেক্টার প্রম্পট",
            descriptionEn = "3D, 2D, or fantasy character design prompts for games",
            descriptionBn = "গেম বা এনিমেশনের জন্য থ্রিডি বা টুডি ক্যারেক্টার প্রম্পট",
            iconName = "Face",
            systemInstructionEn = "You are a game character concept artist. Write character design prompts specifying outfits, facial features, posture, and art style.",
            systemInstructionBn = "আপনি গেম ক্যারেক্টার কনসেপ্ট আর্টিস্ট। থ্রিডি বা কাল্পনিক চরিত্রের পোশাক, চেহারা ও স্টাইল প্রম্পট লিখুন।",
            promptPlaceholderEn = "e.g., Pixar style cute 3D robot doctor holding a syringe",
            promptPlaceholderBn = "যেমন: পিক্সার স্টাইলের কিউট ৩ডি চশমা পরা বিড়াল চরিত্র প্রম্পট",
            promptExamplesEn = listOf("Cyberpunk hacker character with leather jacket and holographic visor", "Ancient wizard with long white beard and glowing magic staff"),
            promptExamplesBn = listOf("বীর বাঙালি মুক্তিযোদ্ধার কাল্পনিক ৩ডি ক্যারেক্টার আর্ট প্রম্পট", "ভবিষ্যতের স্পেস ট্রাভেলার অ্যাস্ট্রোনট চরিত্র"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "prompt_sticker",
            categoryId = "image_prompt",
            titleEn = "Sticker Prompt",
            titleBn = "স্টিকার প্রম্পট",
            descriptionEn = "Vector die-cut stickers with bold outlines and white border",
            descriptionBn = "সাদা বর্ডার সহ ডাই-কাট কিউট ভেক্টর স্টিকার প্রম্পট",
            iconName = "StickyNote2",
            systemInstructionEn = "You are a sticker graphic designer. Craft prompts for vector die-cut stickers with thick white outlines and vibrant flat colors.",
            systemInstructionBn = "আপনি স্টিকার ডিজাইনার। সাদা আউটলাইন ও উজ্জ্বল রঙের কিউট ভেক্টর স্টিকার প্রম্পট বানান।",
            promptPlaceholderEn = "e.g., Cute avocado eating sushi, vector die-cut sticker",
            promptPlaceholderBn = "যেমন: চশমা পরা কিউট কফি কাপের স্টিকার প্রম্পট",
            promptExamplesEn = listOf("Happy Shiba Inu dog wearing a party hat sticker", "Kawaii boba milk tea cup sticker with white border"),
            promptExamplesBn = listOf("ল্যাপটপে কাজ করা কিউট ক্যাট ভেক্টর স্টিকার", "মহাকাশে রকেটে চড়া কিউট প্যান্ডা স্টিকার"),
            tags = listOf("newest")
        ))
        add(AiTool(
            id = "prompt_icon",
            categoryId = "image_prompt",
            titleEn = "Icon Prompt",
            titleBn = "আইকন প্রম্পট",
            descriptionEn = "3D claymorphism or flat UI icon design prompts",
            descriptionBn = "অ্যাপের জন্য ৩ডি ক্লে স্টাইল বা ফ্ল্যাট ইউআই আইকন প্রম্পট",
            iconName = "Apps",
            systemInstructionEn = "You are an icon designer. Generate prompts for 3D claymorphism, glassmorphism, or modern flat app UI icons.",
            systemInstructionBn = "আপনি ইউআই আইকন ডিজাইনার। মোবাইল অ্যাপের জন্য থ্রিডি ক্লেমর্ফিজম বা আধুনিক গ্লাস আইকন প্রম্পট দিন।",
            promptPlaceholderEn = "e.g., 3D clay style weather app icon featuring cloud and sun",
            promptPlaceholderBn = "যেমন: অ্যাপের জন্য থ্রিডি প্লে ট্র্যাশ ক্যান ও ডাস্টবিন আইকন",
            promptExamplesEn = listOf("Isometric 3D shopping cart icon with pastel gradient", "Glassmorphism camera icon with neon purple sheen"),
            promptExamplesBn = listOf("ব্যাংকিং অ্যাপের জন্য ৩ডি গোল্ডেন ওয়ালেট আইকন", "মিউজিক প্লেয়ারের থ্রিডি হোডফোন আইকন প্রম্পট"),
            tags = listOf("trending")
        ))

        // --- CATEGORY 8: CAREER (10 Tools) ---
        add(AiTool(
            id = "career_interview",
            categoryId = "career",
            titleEn = "Interview Questions",
            titleBn = "ইন্টারভিউ প্রশ্ন",
            descriptionEn = "Practice role-specific interview questions & model answers",
            descriptionBn = "বিভিন্ন পদের ইন্টারভিউ প্রশ্ন ও উত্তর অনুশীলন করুন",
            iconName = "RecordVoiceOver",
            systemInstructionEn = "You are an HR hiring manager. Generate common and technical interview questions along with ideal high-scoring STAR method answers.",
            systemInstructionBn = "আপনি এইচআর রিক্রুটার। নির্দিষ্ট পদের জন্য সম্ভাব্য ইন্টারভিউ প্রশ্ন ও STAR মেথডে আদর্শ উত্তর প্রদান করুন।",
            promptPlaceholderEn = "e.g., Junior Android Developer interview questions",
            promptPlaceholderBn = "যেমন: ব্যাংকে ক্যাশ অফিসার পদের ভাইভা প্রশ্ন ও উত্তর",
            promptExamplesEn = listOf("Behavioral interview questions for Team Manager role", "SQL and Database interview questions for Data Analyst"),
            promptExamplesBn = listOf("ডিজিটাল মার্কেটিং এক্সিকিউটিভ ইন্টারভিউ প্রশ্ন উত্তর", "সফটওয়্যার টেস্ট ইঞ্জিনিয়ারিং ভাইভা প্রশ্ন"),
            tags = listOf("popular", "trending")
        ))
        add(AiTool(
            id = "career_resumereview",
            categoryId = "career",
            titleEn = "Resume Review",
            titleBn = "রিজিউম রিভিউ",
            descriptionEn = "Critique and improve your existing CV or resume text",
            descriptionBn = "আপনার বর্তমান সিভির ভুলত্রুটি ও মান উন্নত করুন",
            iconName = "FindInPage",
            systemInstructionEn = "You are an executive resume reviewer. Provide constructive critique on grammar, action verbs, impact metrics, and layout improvements.",
            systemInstructionBn = "আপনি সিভি রিভিউয়ার। আপনার সিভির দুর্বল দিক চিহ্নিত করে শক্তিশালী করার উপায় ও ফিডব্যাক দিন।",
            promptPlaceholderEn = "Paste your CV or resume text here to review...",
            promptPlaceholderBn = "এখানে আপনার সিভির লেখা পেস্ট করুন রিভিউ এর জন্য...",
            promptExamplesEn = listOf("Review my Software Developer resume summary section", "Critique my Sales Manager job bullet points"),
            promptExamplesBn = listOf("আমার সিভির অভিজ্ঞতা সেকশনটি রিভিউ করে দাও", "ফ্রেশার হিসেবে বানানো আমার সিভির মূল্যায়ন"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "career_advice",
            categoryId = "career",
            titleEn = "Career Advice",
            titleBn = "ক্যারিয়ার পরামর্শ",
            descriptionEn = "Guidance on career switching, skill growth & promotions",
            descriptionBn = "ক্যারিয়ার পরিবর্তন, পদোন্নতি ও স্কিল বৃদ্ধির দিকনির্দেশনা",
            iconName = "CompassCalibration",
            systemInstructionEn = "You are a senior career strategist. Provide practical, step-by-step career advice for professional development and industry transitions.",
            systemInstructionBn = "আপনি ক্যারিয়ার পরামর্শক। পদোন্নতি, নতুন স্কিল শেখা বা ক্যারিয়ার ট্রানজিশনের জন্য সুস্পষ্ট গাইডলাইন দিন।",
            promptPlaceholderEn = "e.g., How to transition from Non-Tech field to Web Development?",
            promptPlaceholderBn = "যেমন: পড়াশোনা শেষ করে কীভাবে ফ্রিল্যান্সিং ক্যারিয়ার শুরু করব?",
            promptExamplesEn = listOf("How to negotiate remote work benefits with current employer", "Which skills to learn for AI Engineering career"),
            promptExamplesBn = listOf("সরকারি চাকরি বনাম প্রসেসড কর্পোরেট চাকরির সুবিধা অসুবিধা", "চাকরির পাশাপাশি সাইড ইনকাম শুরু করার উপায়"),
            tags = listOf("trending")
        ))
        add(AiTool(
            id = "career_salary",
            categoryId = "career",
            titleEn = "Salary Negotiation",
            titleBn = "বেতন আলোচনা",
            descriptionEn = "Scripts & strategies to negotiate higher job offers",
            descriptionBn = "নতুন চাকরি বা প্রমোশনে বেশি বেতন চাওয়ার কৌশল ও বার্তা",
            iconName = "AttachMoney",
            systemInstructionEn = "You are a salary negotiation coach. Provide scripts, email templates, and psychological tactics to negotiate better compensation packages.",
            systemInstructionBn = "আপনি স্যালারি নেগোশিয়েটর। দক্ষতার সাথে বেতনের প্যাকেজ ও বোনাস বৃদ্ধির জন্য কথা বলার স্ক্রিপ্ট তৈরি করুন।",
            promptPlaceholderEn = "e.g., Negotiating job offer from $50k to $60k",
            promptPlaceholderBn = "যেমন: ইন্টারভিউ শেষে কাঙ্ক্ষিত বেতন চাওয়ার ভদ্র ইমেইল",
            promptExamplesEn = listOf("Asking for annual salary raise during performance review", "Negotiating signing bonus for senior engineer role"),
            promptExamplesBn = listOf("বাৎসরিক পারফরম্যান্স রিভিউতে বেতন বাড়ানোর প্রস্তাব", "অফার লেটারে কম বেতন দিলে সেটা বাড়ানোর অনুরোধ"),
            tags = listOf("newest")
        ))
        add(AiTool(
            id = "career_linkedin",
            categoryId = "career",
            titleEn = "LinkedIn Summary",
            titleBn = "লিঙ্কডইন সামারি",
            descriptionEn = "Compelling 'About' section for your LinkedIn profile",
            descriptionBn = "লিঙ্কডইন প্রোফাইলের আকর্ষণীয় 'About' সেকশন লিখুন",
            iconName = "Person",
            systemInstructionEn = "You are a personal branding consultant. Write a storytelling, high-converting LinkedIn About/Summary section that hooks recruiters.",
            systemInstructionBn = "আপনি প্রোফাইল এক্সপার্ট। রিক্রুটার ও ক্লায়েন্টকে আকৃষ্ট করার উপযোগী লিঙ্কডইন অ্যাবাউট সামারি রচনা করুন।",
            promptPlaceholderEn = "e.g., Full Stack Developer specializing in React, Node & Cloud",
            promptPlaceholderBn = "যেমন: ৪ বছরের অভিজ্ঞ ডিজিটাল মার্কেটরের লিঙ্কডইন বায়ো ও সামারি",
            promptExamplesEn = listOf("Data Scientist transition summary for LinkedIn", "UX Researcher story-driven LinkedIn summary"),
            promptExamplesBn = listOf("গ্রাফিক্স ডিজাইনারের পোর্টফোলিও সামারি লিঙ্কডইন", "ইউনিভার্সিটি গ্রাজুয়েটের লিঙ্কডইন প্রোফাইল সামারি"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "career_portfolio",
            categoryId = "career",
            titleEn = "Portfolio Writer",
            titleBn = "পোর্টফোলিও রাইটার",
            descriptionEn = "Case study descriptions & project intros for portfolios",
            descriptionBn = "পোর্টফোলিও ওয়েবসাইটের প্রজেক্ট বিবরণী ও কেস স্টাডি",
            iconName = "FolderSpecial",
            systemInstructionEn = "You are a portfolio copywriter. Write structured project case studies highlighting Problem, Solution, Technologies Used, and Results achieved.",
            systemInstructionBn = "আপনি পোর্টফোলিও কন্টেন্ট রাইটার। ক্লায়েন্টকে প্রভাবিত করার মতো প্রজেক্টের সমস্যা, সমাধান ও ফলাফলের কেস স্টাডি লিখুন।",
            promptPlaceholderEn = "e.g., E-commerce Redesign project case study for UX designer",
            promptPlaceholderBn = "যেমন: ফুড ডেলিভারি অ্যাপ প্রজেক্টের পোর্টফোলিও বিবরণী",
            promptExamplesEn = listOf("Mobile Banking App security audit project summary", "Real Estate Website development project details"),
            promptExamplesBn = listOf("লোগো রিডিজাইন প্রজেক্টের কেস স্টাডি", "রেস্টুরেন্ট বিলিং সফটওয়্যারের কেস স্টাডি"),
            tags = listOf("newest")
        ))
        add(AiTool(
            id = "career_jobemail",
            categoryId = "career",
            titleEn = "Job Email",
            titleBn = "জব ইমেইল",
            descriptionEn = "Follow-up, resignation, or job acceptance emails",
            descriptionBn = "চাকরি ছাড়া, জয়েন করা বা ফলোআপ সংক্রান্ত ইমেইল",
            iconName = "Mail",
            systemInstructionEn = "You are a professional HR correspondent. Draft professional job application follow-ups, offer acceptances, or resignation letters.",
            systemInstructionBn = "আপনি প্রফেশনাল রাইটার। ইস্তফাপত্র, চাকরি গ্রহণের স্বীকৃতি বা ফলোআপ ইমেইল তৈরি করুন।",
            promptPlaceholderEn = "e.g., 2 weeks notice resignation letter due to better opportunity",
            promptPlaceholderBn = "যেমন: ২ সপ্তাহের নোটিশে চাকরি ছাড়ার প্রফেশনাল পদত্যাগপত্র",
            promptExamplesEn = listOf("Accepting job offer email template", "Declining job offer politely email template"),
            promptExamplesBn = listOf("অফার লেটার পাওয়ার পর ধন্যবাদ ও জয়েনিং ইমেইল", "ইন্টারভিউ এর ২ সপ্তাহ পর খোঁজ নেয়ার ইমেইল"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "career_roadmap",
            categoryId = "career",
            titleEn = "Skills Roadmap",
            titleBn = "স্কিলস রোডম্যাপ",
            descriptionEn = "Step-by-step learning roadmap for any profession",
            descriptionBn = "যেকোনো পেশায় দক্ষ হওয়ার ধাপে ধাপে লার্নিং রোডম্যাপ",
            iconName = "AltRoute",
            systemInstructionEn = "You are a career learning architect. Generate a structured 3 to 6-month skill development roadmap with resources, projects, and milestones.",
            systemInstructionBn = "আপনি ক্যারিয়ার আর্কিটেক্ট। আগামী ৬ মাসে নির্দিষ্ট পেশায় জিরো থেকে প্রো হওয়ার ক্রমানুযায়ী লার্নিং রোডম্যাপ দিন।",
            promptPlaceholderEn = "e.g., Roadmap to become a DevOps Engineer in 6 months",
            promptPlaceholderBn = "যেমন: ৬ মাসে সাইবার সিকিউরিটি এক্সপার্ট হওয়ার রোডম্যাপ",
            promptExamplesEn = listOf("Roadmap to learn Data Science starting from Scratch", "Roadmap to become a UI/UX Designer"),
            promptExamplesBn = listOf("ফুল স্ট্যাক ওয়েব ডেভেলপমেন্ট শেখার পূর্ণাঙ্গ রোডম্যাপ", "ডিজিটাল মার্কেটিং মাস্টার করার গাইডলাইন"),
            tags = listOf("popular", "trending")
        ))
        add(AiTool(
            id = "career_goal",
            categoryId = "career",
            titleEn = "Goal Planner",
            titleBn = "গোল প্ল্যানার",
            descriptionEn = "SMART goal setting framework for career milestone achievements",
            descriptionBn = "ক্যারিয়ারের লক্ষ্য অর্জনের SMART গোল প্লাান",
            iconName = "TrackChanges",
            systemInstructionEn = "You are a executive performance coach. Break down vague ambitions into SMART (Specific, Measurable, Achievable, Relevant, Time-bound) career goals.",
            systemInstructionBn = "আপনি পারফরম্যান্স কোচ। ক্যারিয়ারের লক্ষ্যকে পরিমাপযোগ্য ছোট ছোট মাইলস্টোনে ভাগ করে দিন।",
            promptPlaceholderEn = "e.g., Goal: Getting promoted to Senior Manager within 1 year",
            promptPlaceholderBn = "যেমন: আগামী ১ বছরের মধ্যে নিজের এজেন্সির প্রথম ৫০ জন ক্লায়েন্ট পাওয়া",
            promptExamplesEn = listOf("Goal: Publish 12 technical articles this year", "Goal: Transition to 100% remote working job"),
            promptExamplesBn = listOf("চলতি বছরে ৫টি প্রফেশনাল সার্টিফিকেট অর্জনের গোল প্ল্যান", "নতুন ব্যাকএন্ড টেকনোলজি শেখার লক্ষ্যমাত্রা"),
            tags = listOf("newest")
        ))
        add(AiTool(
            id = "career_hranswers",
            categoryId = "career",
            titleEn = "HR Answers",
            titleBn = "এইচআর অ্যান্সার",
            descriptionEn = "How to handle tough interview questions like 'Tell me about yourself'",
            descriptionBn = "কঠিন ইন্টারভিউ প্রশ্ন যেমন 'নিজের সম্পর্কে বলুন' এর সেরা উত্তর",
            iconName = "ContactSupport",
            systemInstructionEn = "You are a career interview coach. Draft compelling answers to tough questions like 'What is your biggest weakness?' or 'Why should we hire you?'.",
            systemInstructionBn = "আপনি ভাইভা কোচ। 'আপনার দুর্বলতা কী?' বা 'কেন আপনাকে নেব?' এর বুদ্ধিমান উত্তর বানিয়ে দিন।",
            promptPlaceholderEn = "e.g., How to answer 'Where do you see yourself in 5 years?'",
            promptPlaceholderBn = "যেমন: 'নিজের সম্পর্কে সংক্ষেপে কিছু বলুন' এর আকর্ষণীয় উত্তর",
            promptExamplesEn = listOf("How to explain a 1-year career gap in interview", "How to answer 'Why are you leaving your current company?'"),
            promptExamplesBn = listOf("ইন্টারভিউতে পড়াশোনার গ্যাপ বা বিরতি কীভাবে ব্যাখ্যা করবেন", "অন্য প্রার্থীরা আপনার চেয়ে এগিয়ে থাকলে কী বলবেন"),
            tags = listOf("trending")
        ))

        // --- CATEGORY 9: DAILY LIFE (10 Tools) ---
        add(AiTool(
            id = "daily_travel",
            categoryId = "daily",
            titleEn = "Travel Planner",
            titleBn = "ভ্রমণ প্ল্যানার",
            descriptionEn = "Day-by-day travel itineraries with places, budget & food",
            descriptionBn = "দিনভিত্তিক ভ্রমণ গাইডলাইন, স্পট, বাজেট ও খাওয়া-দাওয়া",
            iconName = "FlightTakeoff",
            systemInstructionEn = "You are a travel guide author. Create detailed day-by-day travel itineraries including sightseeing, local foods, transport, and budget estimates.",
            systemInstructionBn = "আপনি ট্রাভেল গাইড। বাজেট, যাতায়াত, দর্শনীয় স্থান ও খাওয়ার তালিকা সহ চমৎকার ট্যুর প্ল্যান বানিয়ে দিন।",
            promptPlaceholderEn = "e.g., 3-day budget trip to Sylhet with friends",
            promptPlaceholderBn = "যেমন: বন্ধুদের সাথে বান্দরবান ৩ দিন ২ রাতের বাজেট ট্যুর প্ল্যান",
            promptExamplesEn = listOf("5-day family trip itinerary to Bangkok, Thailand", "Weekend getaway travel plan to Cox's Bazar"),
            promptExamplesBn = listOf("সাজেক ভ্যালি ২ দিনের পারফেক্ট ভ্রমণ গাইড", "শ্রীমঙ্গল ও লাউয়াছড়া জাতীয় উদ্যান ১ দিনের প্ল্যান"),
            tags = listOf("popular", "trending")
        ))
        add(AiTool(
            id = "daily_workout",
            categoryId = "daily",
            titleEn = "Workout Plan",
            titleBn = "ওয়ার্কআউট প্ল্যান",
            descriptionEn = "Home or gym weekly fitness routines tailored to goals",
            descriptionBn = "বাসায় বা জিমে ব্যায়াম করার সাপ্তাহিক ফিটনেস রুটিন",
            iconName = "FitnessCenter",
            systemInstructionEn = "You are a certified fitness trainer. Design weekly workout programs detailing exercises, sets, reps, and rest periods for home or gym.",
            systemInstructionBn = "আপনি ফিটনেস ট্রেইনার। ওজন কমানো বা মাসল বিল্ডিং এর জন্য সেট ও রেপ্স সহ এক্সারসাইজ রুটিন দিন।",
            promptPlaceholderEn = "e.g., 4-day dumbbell only workout plan for weight loss",
            promptPlaceholderBn = "যেমন: বাসায় কোনো ইক্যুপমেন্ট ছাড়া মেদ কমানোর সাপ্তাহিক ব্যায়াম",
            promptExamplesEn = listOf("3-day Push/Pull/Legs gym workout routine", "15 minute daily cardio home routine"),
            promptExamplesBn = listOf("ওজন বাড়ানোর জন্য জিমে ব্যায়ামের রুটিন", "পেটের মেদ কমানোর ১০ মিনিটের ডেইলি এক্সারসাইজ"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "daily_meal",
            categoryId = "daily",
            titleEn = "Meal Planner",
            titleBn = "মিল প্ল্যানার",
            descriptionEn = "Balanced healthy diet plans for breakfast, lunch & dinner",
            descriptionBn = "সকালের নাস্তা, দুপুর ও রাতের সুষম পুষ্টিকর ডায়েট চার্ট",
            iconName = "Restaurant",
            systemInstructionEn = "You are a clinical nutritionist. Generate healthy, balanced weekly meal prep menus specifying calorie ranges and macronutrient goals.",
            systemInstructionBn = "আপনি নিউট্রিশনিস্ট। পুষ্টিগুণ বজায় রেখে প্রতিদিনের সুষম খাবার ও ডায়েট চার্ট তৈরি করুন।",
            promptPlaceholderEn = "e.g., 7-day high protein South Asian diet plan for fat loss",
            promptPlaceholderBn = "যেমন: ডায়াবেটিস রোগীদের জন্য স্বাস্থ্যকর এক সপ্তাহের খাবারের তালিকা",
            promptExamplesEn = listOf("1800 calorie daily meal menu plan", "Vegetarian weekly lunch and dinner prep guide"),
            promptExamplesBn = listOf("কম খরচে পুষ্টিকর খাবার নিয়ে ১ সপ্তাহের মিল প্ল্যান", "ওজন কমানোর জন্য স্বল্প ক্যালোরির বাংলাদেশি খাবার"),
            tags = listOf("trending")
        ))
        add(AiTool(
            id = "daily_budget",
            categoryId = "daily",
            titleEn = "Budget Planner",
            titleBn = "বাজেট প্ল্যানার",
            descriptionEn = "50/30/20 rule monthly personal budget allocation",
            descriptionBn = "মাসিক আয়-ব্যয়ের হিসাব ও ৫০/৩০/২০ নিয়মে সঞ্চয় বাজেট",
            iconName = "AccountBalanceWallet",
            systemInstructionEn = "You are a personal financial advisor. Allocate monthly income into Needs, Wants, and Savings using the 50/30/20 financial rule.",
            systemInstructionBn = "আপনি আর্থিক পরামর্শক। মাসিক আয় থেকে খরচ, প্রয়োজন ও সঞ্চয় ভাগ করে স্মার্ট বাজেট প্ল্যান বানিয়ে দিন।",
            promptPlaceholderEn = "e.g., How to manage a monthly salary of 40,000 BDT?",
            promptPlaceholderBn = "যেমন: ৩০,০০০ টাকা মাসিক বেতনে পরিবার সামলানোর বাজেট চার্ট",
            promptExamplesEn = listOf("Budget allocation for 50,000 BDT single professional", "How to save 20% of salary every month"),
            promptExamplesBn = listOf("স্টুডেন্ট মেসে খরচের বাজেট নিয়ন্ত্রণ করার টিপস", "মাসিক ৫০,০০০ টাকা আয়ে সঞ্চয় বাড়ানোর উপায়"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "daily_shopping",
            categoryId = "daily",
            titleEn = "Shopping List",
            titleBn = "শপিং লিস্ট",
            descriptionEn = "Categorized grocery & household shopping lists",
            descriptionBn = "কাঁচাবাজার ও সংসারের কেনাকাটার ক্যাটাগরিভিত্তিক তালিকা",
            iconName = "ShoppingCart",
            systemInstructionEn = "You are an organized household planner. Create categorized grocery and household shopping lists organized by store aisle.",
            systemInstructionBn = "আপনি সংসার পরিচালক। সহজে কেনাকাটার সুবিধার্থে বাজার ও মুদির সদায়ের তালিকা গুছিয়ে দিন।",
            promptPlaceholderEn = "e.g., Monthly grocery shopping list for a family of 4",
            promptPlaceholderBn = "যেমন: ৪ জনের পরিবারের জন্য ১ মাসের কাঁচাবাজারের শপিং লিস্ট",
            promptExamplesEn = listOf("Essential baking ingredients shopping checklist", "First apartment move-in kitchen items list"),
            promptExamplesBn = listOf("ঈদের কেনাকাটার প্রয়োজনীয় শপিং লিস্ট", "মেসের ১ সপ্তাহের বাজার তালিকার চেকক্লিস্ট"),
            tags = listOf("newest")
        ))
        add(AiTool(
            id = "daily_recipe",
            categoryId = "daily",
            titleEn = "Recipe Generator",
            titleBn = "রেসিপি জেনারেটর",
            descriptionEn = "Step-by-step cooking recipes based on available ingredients",
            descriptionBn = "ঘরে থাকা উপাদান দিয়ে সুস্বাদু রান্নার সহজ রেসিপি",
            iconName = "SoupKitchen",
            systemInstructionEn = "You are a professional chef. Create mouth-watering, step-by-step cooking recipes based on listed ingredients.",
            systemInstructionBn = "আপনি শেফ। ঘরে থাকা মশলা ও সবজি দিয়ে ধাপে ধাপে সুস্বাদু রান্নার রেসিপি প্রস্তুত করুন।",
            promptPlaceholderEn = "e.g., I have chicken, potatoes, onion, and yogurt. What can I cook?",
            promptPlaceholderBn = "যেমন: ডিম, আলু ও পেঁয়াজ দিয়ে চটজলদি বিকেলের নাস্তার রেসিপি",
            promptExamplesEn = listOf("Authentic Bangladeshi Kacchi Biryani recipe", "15-minute quick pasta recipe for dinner"),
            promptExamplesBn = listOf("রেস্টুরেন্ট স্টাইলের চিকেন ফ্রাই রান্নার রেসিপি", "কম তেলে ইলিশ মাছ ভুনার সহজ নিয়ম"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "daily_gift",
            categoryId = "daily",
            titleEn = "Gift Ideas",
            titleBn = "উপহারের আইডিয়া",
            descriptionEn = "Thoughtful gift ideas based on age, gender & occasion",
            descriptionBn = "বয়স, পছন্দ ও অনুষ্ঠান অনুযায়ী সেরা উপহারের আইডিয়া",
            iconName = "CardGiftcard",
            systemInstructionEn = "You are a gift personalizing expert. Suggest thoughtful, creative gift options categorized by budget, recipient interests, and occasion.",
            systemInstructionBn = "আপনি গিফট কনসালট্যান্ট। উপলক্ষ ও বাজেট অনুযায়ী অনন্য ও আকর্ষণীয় উপহারের তালিকা প্রদান করুন।",
            promptPlaceholderEn = "e.g., Birthday gift for a tech-savvy friend under $50",
            promptPlaceholderBn = "যেমন: বন্ধুর বিয়েতে ১,০০০-২,০০০ টাকার মধ্যে সেরা উপহারের আইডিয়া",
            promptExamplesEn = listOf("Anniversary gift for husband who loves reading", "Graduation gift ideas for college student"),
            promptExamplesBn = listOf("মায়ের জন্মদিনে দেওয়া যায় এমন বিশেষ উপহার", "ছোট ভাইয়ের এসএসসি পাসের খুশিতে উপহার আইডিয়া"),
            tags = listOf("trending")
        ))
        add(AiTool(
            id = "daily_habit",
            categoryId = "daily",
            titleEn = "Habit Tracker",
            titleBn = "হ্যাবিট ট্র্যাকার",
            descriptionEn = "Daily habit building routines with cue, routine & reward",
            descriptionBn = "ভাল অভ্যাস গড়া ও খারাপ অভ্যাস ত্যাগের ট্র্যাকিং প্ল্যান",
            iconName = "Checklist",
            systemInstructionEn = "You are a habit behavioral coach. Design habit building routines based on Atomic Habits (Cue, Craving, Routine, Reward).",
            systemInstructionBn = "আপনি অভ্যাস গঠন প্রশিক্ষক। ছোট ছোট অভ্যাস তৈরির মাধ্যমে জীবনযাত্রার মান উন্নত করার প্ল্যান বানান।",
            promptPlaceholderEn = "e.g., Building a habit to read 20 pages of a book every night",
            promptPlaceholderBn = "যেমন: প্রতিদিন ১ ঘণ্টা ব্যায়াম করার স্থায়ী অভ্যাস গড়ে তোলার ট্র্যাকার",
            promptExamplesEn = listOf("How to break smartphone addiction before sleep", "Daily habit system for drinking 3 liters of water"),
            promptExamplesBn = listOf("ফোন ব্যবহারের আসক্তি কমানোর অভ্যাস গঠন", "প্রতিদিন সকালে ৬টায় ঘুম থেকে ওঠার হ্যাবিট ট্র্যাকার"),
            tags = listOf("newest")
        ))
        add(AiTool(
            id = "daily_planner",
            categoryId = "daily",
            titleEn = "Daily Planner",
            titleBn = "দৈনিক প্ল্যানার",
            descriptionEn = "Time-blocked hourly schedule for maximum productivity",
            descriptionBn = "সারাদিনের সময়ভিত্তিক টাইম ব্লকিং ও কাজের তালিকা",
            iconName = "Today",
            systemInstructionEn = "You are a productivity expert. Design an hourly time-blocked daily schedule balancing focus work, breaks, and personal time.",
            systemInstructionBn = "আপনি প্রোডাক্টিভিটি এক্সপার্ট। সময় নষ্ট না করে সারাদিনের কাজগুলোকে ঘণ্টায় ঘণ্টায় ভাগ করার রুটিন লিখুন।",
            promptPlaceholderEn = "e.g., Schedule for a busy workday with 3 major priority tasks",
            promptPlaceholderBn = "যেমন: ছুটির দিনে পড়াশোনা ও রিফ্রেশমেন্টের দৈনন্দিন প্ল্যান",
            promptExamplesEn = listOf("Productive Saturday routine for self-improvement", "Work-from-home hourly schedule"),
            promptExamplesBn = listOf("পরীক্ষার আগের দিনের বিশেষ সময়সূচি", "সকাল ৮টা থেকে রাত ১০টার কাজের ব্যালেন্সড রুটিন"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "daily_event",
            categoryId = "daily",
            titleEn = "Event Planner",
            titleBn = "ইভেন্ট প্ল্যানার",
            descriptionEn = "Checklists, timeline & guest management for parties",
            descriptionBn = "অনুষ্ঠান বা পার্টির পরিকল্পনা, চেকলিস্ট ও খাবারের বাজেট",
            iconName = "Celebration",
            systemInstructionEn = "You are an event coordinator. Draft event organization checklists including venue setup, invitations, catering, and schedule timeline.",
            systemInstructionBn = "আপনি ইভেন্ট অর্গানাইজার। জন্মদিন বা পারিবারিক অনুষ্ঠানের খুঁটিনাটি চেকলিস্ট ও সময়সূচি প্রস্তুত করুন।",
            promptPlaceholderEn = "e.g., Organizing a surprise birthday party for 20 guests",
            promptPlaceholderBn = "যেমন: ২০ জন অতিথির গেট-টুগেদার পার্টির পরিকল্পনা ও বাজেট",
            promptExamplesEn = listOf("Corporate team picnic event checklist and agenda", "Small home housewarming party planning guide"),
            promptExamplesBn = listOf("অফিসের ইফতার পার্টি আয়োজনের প্ল্যান", "শিশুর প্রথম জন্মদিনের থিম পার্টি আয়োজন"),
            tags = listOf("trending")
        ))

        // --- CATEGORY 10: FUN (10 Tools) ---
        add(AiTool(
            id = "fun_jokes",
            categoryId = "fun",
            titleEn = "Jokes",
            titleBn = "কৌতুক / জোকস",
            descriptionEn = "Clean, funny jokes, puns and humor",
            descriptionBn = "হাস্যরসাত্মক মজার কৌতুক ও জোকস",
            iconName = "SentimentVerySatisfied",
            systemInstructionEn = "You are a stand-up comedian. Tell funny, clean, lighthearted jokes and clever puns.",
            systemInstructionBn = "আপনি হাসির জাদুকর। মার্জিত ও পেটে খিল ধরা মজার কৌতুক ও জোকস পরিবেশন করুন।",
            promptPlaceholderEn = "e.g., Tell me 3 funny jokes about programmers",
            promptPlaceholderBn = "যেমন: ডাক্তার ও রোগী নিয়ে ৩টি মজার জোকস শোনাও",
            promptExamplesEn = listOf("5 funny puns about food and cooking", "Funny jokes about school teachers and students"),
            promptExamplesBn = listOf("বল্টু ও মাস্টারের সেরা ৩টি হাসির কৌতুক", "স্বামী-স্ত্রীর মজার হাসির গল্প"),
            tags = listOf("popular", "trending")
        ))
        add(AiTool(
            id = "fun_riddles",
            categoryId = "fun",
            titleEn = "Riddles",
            titleBn = "ধাঁধা",
            descriptionEn = "Tricky brain teasers and riddles with hidden answers",
            descriptionBn = "বুদ্ধির কসরত ও উত্তরের জন্য ঢাকা মজার বাংলা ধাঁধা",
            iconName = "Help",
            systemInstructionEn = "You are a riddle master. Present clever, tricky riddles and brain teasers. Hide the answers below spoiler formatting.",
            systemInstructionBn = "আপনি ধাঁধা সম্রাট। বন্ধুদের সাথে খেলার মতো চমৎকার ধাঁধা জিজ্ঞেস করুন এবং নিচে উত্তর লুকিয়ে রাখুন।",
            promptPlaceholderEn = "e.g., Give me 3 tricky riddles about time and nature",
            promptPlaceholderBn = "যেমন: বুদ্ধির পরীক্ষা নেওয়ার মতো ৫টি কঠিন ধাঁধা উত্তরসহ",
            promptExamplesEn = listOf("5 classic brain teasers for kids", "Tricky math riddles that sound impossible"),
            promptExamplesBn = listOf("গ্রামবাংলার ঐতিহ্যবাহী ৫টি মজার ধাঁধা", "যা আছে কিন্তু দেখা যায় না এমন ৫টি ধাঁধা"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "fun_truthordare",
            categoryId = "fun",
            titleEn = "Truth or Dare",
            titleBn = "ট্রুথ অর ড্যার",
            descriptionEn = "Fun, safe Truth or Dare questions for friends gathering",
            descriptionBn = "আড্ডায় বন্ধুদের সাথে ট্রুথ অর ড্যার খেলার প্রশ্ন ও চ্যালেঞ্জ",
            iconName = "Casino",
            systemInstructionEn = "You are a party game host. Generate fun, creative, safe Truth questions and harmless Dare challenges for social gatherings.",
            systemInstructionBn = "আপনি গেম হোস্ট। বন্ধুদের আড্ডায় খেলার জন্য ট্রুথ প্রশ্ন ও ড্যার চ্যালেঞ্জের তালিকা দিন।",
            promptPlaceholderEn = "e.g., 5 Truth questions and 5 Dares for university friends",
            promptPlaceholderBn = "যেমন: বন্ধুদের সাথে আড্ডার ১০টি মজার ট্রুথ প্রশ্ন ও ড্যার",
            promptExamplesEn = listOf("Fun truths and dares for family game night", "Hilarious dare ideas for a party"),
            promptExamplesBn = listOf("কাছের বন্ধুদের সিক্রেট জানার ৫টি ট্রুথ প্রশ্ন", "মজার ও নির্দোষ ৫টি ড্যার চ্যালেঞ্জ"),
            tags = listOf("trending")
        ))
        add(AiTool(
            id = "fun_roast",
            categoryId = "fun",
            titleEn = "Roast Generator",
            titleBn = "রোস্ট জেনারেটর",
            descriptionEn = "Playful, lighthearted comedy roasts of hobbies or habits",
            descriptionBn = "অভ্যাস বা শখের ওপর হাল্কা মজার কমেডি রোস্ট (মজা করার জন্য)",
            iconName = "LocalFireDepartment",
            systemInstructionEn = "You are a friendly comedy roaster. Deliver hilarious, good-natured, harmless roasts without hate speech or real offense.",
            systemInstructionBn = "আপনি রসিক ফ্রেন্ড। কোনো কটু কথা না বলে শখ বা স্বভাবের ওপর বন্ধুসুলভ মজার রোস্ট পরিবেশন করুন।",
            promptPlaceholderEn = "e.g., Roast someone who spends 6 hours every day playing video games",
            promptPlaceholderBn = "যেমন: যিনি সারাদিন চা পান করেন তাকে নিয়ে বন্ধুসুলভ রোস্ট",
            promptExamplesEn = listOf("Roast a friend who is always 30 minutes late", "Roast people who take 50 photos before eating dinner"),
            promptExamplesBn = listOf("যিনি সবসময় বলেন 'কাল থেকে ডায়েট শুরু করব' তাকে নিয়ে রোস্ট", "গানের সুর না জেনেও বাথরুমে গান গাওয়া বন্ধুদের রোস্ট"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "fun_fantasy",
            categoryId = "fun",
            titleEn = "Fantasy Names",
            titleBn = "ফ্যান্টাসি নাম",
            descriptionEn = "Names for RPG games, dragons, kingdoms & superheroes",
            descriptionBn = "গেম, ড্রাগন, রাজ্য বা কাল্পনিক হিরোর নান্দনিক নাম",
            iconName = "AutoAwesome",
            systemInstructionEn = "You are a fantasy worldbuilder. Generate epic, mystical, sound-appealing names for RPG characters, kingdoms, and dragons.",
            systemInstructionBn = "আপনি কাল্পনিক রাজ্য রচয়িতা। গেম বা রূপকথার চরিত্র ও রাজ্যের জন্য রাজকীয় নাম বানান।",
            promptPlaceholderEn = "e.g., 10 names for an ancient elven kingdom in the mountains",
            promptPlaceholderBn = "যেমন: জাদুকরী কাল্পনিক কোনো জাদুকর বা রাজকুমারের নাম",
            promptExamplesEn = listOf("10 cool superhero hero and villain codenames", "10 epic dragon names with meanings"),
            promptExamplesBn = listOf("অনলাইন গেমের ক্যারেক্টারের জন্য ১০টি কুল নাম", "ফ্যান্টাসি উপন্যাসের রাজ্যের নাম"),
            tags = listOf("newest")
        ))
        add(AiTool(
            id = "fun_nicknames",
            categoryId = "fun",
            titleEn = "Nicknames",
            titleBn = "নিকনেম",
            descriptionEn = "Cute, funny or gaming nicknames for friends & gaming IDs",
            descriptionBn = "বন্ধু, গেমিং আইডি বা প্রিয়জনের জন্য মিষ্টি ও কিউট ডাকনাম",
            iconName = "Face5",
            systemInstructionEn = "You are a nickname generator. Suggest cute, funny, cool, or unique nicknames based on personality traits or gaming styles.",
            systemInstructionBn = "আপনি নাম পরামর্শক। ব্যক্তিত্ব ও স্বভাবের ওপর ভিত্তি করে মজার, মিষ্টি বা গেমিং ডাকনাম উপহার দিন।",
            promptPlaceholderEn = "e.g., Gaming nickname for a fast sniper player",
            promptPlaceholderBn = "যেমন: সবসময় ঘুমকাতুরে বন্ধুর জন্য একটি মজার ডাকনাম",
            promptExamplesEn = listOf("Cute nicknames for best friends", "Cool gaming IDs for PUBG and Free Fire"),
            promptExamplesBn = listOf("পাবজি বা ফ্রি ফায়ারের জন্য ১০টি আটিচ্যুড গেমিং নাম", "সবসময় হাসিখুশি বন্ধুর কিউট নিকনেম"),
            tags = listOf("trending")
        ))
        add(AiTool(
            id = "fun_emojistory",
            categoryId = "fun",
            titleEn = "Emoji Story",
            titleBn = "ইমোজি স্টোরি",
            descriptionEn = "Tell complete short stories using only emojis",
            descriptionBn = "শব্দ ছাড়া শুধু ইমোজি দিয়ে আস্ত ছোটগল্প ও উত্তর",
            iconName = "SentimentSatisfiedAlt",
            systemInstructionEn = "You are an emoji storyteller. Craft creative short stories told strictly or primarily through sequences of emojis.",
            systemInstructionBn = "আপনি ইমোজি গল্পকার। মজার ছোট গল্পকে একের পর এক সুন্দর ইমোজি দ্বারা ফুটিয়ে তুলুন এবং নিচে গল্পটি কী তা সংক্ষেপে লিখুন।",
            promptPlaceholderEn = "e.g., Tell the story of Titanic movie using emojis",
            promptPlaceholderBn = "যেমন: আলাদিনের জাদুকরী প্রদীপের গল্পটি ইমোজি দিয়ে প্রকাশ কর",
            promptExamplesEn = listOf("Tell Cinderella story in emojis", "Tell a story about a space astronaut landing on alien planet in emojis"),
            promptExamplesBn = listOf("ফুটবল বিশ্বকাপ জয়ের গল্প ইমোজিতে", "জঙ্গল ট্রিপে বাঘের মুখে পড়ার থ্রিলার গল্প ইমোজিতে"),
            tags = listOf("newest")
        ))
        add(AiTool(
            id = "fun_quiz",
            categoryId = "fun",
            titleEn = "Quiz",
            titleBn = "মজার কুইজ",
            descriptionEn = "Fun personality quizzes like 'Which Marvel Superhero are you?'",
            descriptionBn = "আপনার ব্যক্তিত্ব জানার মতো মজার ও কৌতূহল উদ্দীপক কুইজ",
            iconName = "VideogameAsset",
            systemInstructionEn = "You are a Buzzfeed style quiz master. Create fun personality quizzes with scoring options that reveal a fun character fit.",
            systemInstructionBn = "আপনি বিনোদনমূলক কুইজ রচয়িতা। 'আপনি কোন ধরনের পর্যটক?' টাইপের ৩টি প্রশ্নের পার্সোনালিটি কুইজ বানান।",
            promptPlaceholderEn = "e.g., Quiz: Which fictional movie character matches your personality?",
            promptPlaceholderBn = "যেমন: কুইজ: আপনার খাবারের পছন্দ অনুযায়ী আপনি কত শতাংশ ভোজনরসিক?",
            promptExamplesEn = listOf("Quiz: What type of coffee best describes you?", "Quiz: Which Hogwarts house do you belong to?"),
            promptExamplesBn = listOf("কুইজ: আপনি কেমন ধরনের বন্ধু?", "কুইজ: গেমার হিসেবে আপনার ব্যক্তিত্ব কেমন?"),
            tags = listOf("popular")
        ))
        add(AiTool(
            id = "fun_pickuplines",
            categoryId = "fun",
            titleEn = "Pickup Lines",
            titleBn = "পিকআপ লাইন",
            descriptionEn = "Cheesy, sweet or funny romantic pickup lines",
            descriptionBn = "মিষ্টি, মজার ও কৌতুকপূর্ণ রোমান্টিক পিকআপ লাইন",
            iconName = "Favorite",
            systemInstructionEn = "You are a charming romantic poet. Craft sweet, funny, clever, or playfully cheesy pickup lines.",
            systemInstructionBn = "আপনি রোমান্টিক বার্তা লেখক। প্রিয়জনের মুখে হাসি ফোটানোর মতো সুন্দর, মজার ও মিষ্টি পিকআপ লাইন লিখুন।",
            promptPlaceholderEn = "e.g., 5 clever cheesy pickup lines about coding or tech",
            promptPlaceholderBn = "<ctrl42>যেমন: বই বা সাহিত্য ভালোবাসেন এমন মানুষের জন্য প্রীতিপূর্ণ পিকআপ লাইন",
            promptExamplesEn = listOf("5 cute science themed pickup lines", "5 funny lighthearted coffee pickup lines"),
            promptExamplesBn = listOf("চা প্রেমীদের জন্য মজার ও মিষ্টি ৫টি পিকআপ লাইন", "পদার্থবিজ্ঞান ও গণিত থিমের মজার পিকআপ লাইন"),
            tags = listOf("trending")
        ))
        add(AiTool(
            id = "fun_random",
            categoryId = "fun",
            titleEn = "Random Ideas",
            titleBn = "র্যান্ডম আইডিয়া",
            descriptionEn = "Sparks of random creative ideas for art, story or weekend fun",
            descriptionBn = "সপ্তাহান্তে করার মতো মজাদার ও অদ্ভুত সৃজনশীল আইডিয়া",
            iconName = "AutoMode",
            systemInstructionEn = "You are a creative inspiration spark. Generate wild, imaginative, fun random ideas for weekend projects, art, or games.",
            systemInstructionBn = "আপনি চিন্তার খোরাক প্রদানকারী। বিষণ্ণতা কাটাতে বা নতুন কিছু করতে ৫টি চমৎকার ভিন্নধর্মী আইডিয়া উপস্থাপন করুন।",
            promptPlaceholderEn = "e.g., 5 unique things to do on a rainy Sunday afternoon",
            promptPlaceholderBn = "যেমন: সাপ্তাহিক ছুটির দিনে বন্ধুদের নিয়ে করার মতো ৫টি অদ্ভুত মজার কাজ",
            promptExamplesEn = listOf("5 crazy science experiment ideas to try with household items", "5 creative drawing challenges for bored artists"),
            promptExamplesBn = listOf("বিনা খরচে ঘরে বসে সময় কাটানোর ৫টি মজাদার পদ্ধতি", "মোবাইল দিয়ে আর্ট করার ৫টি ইউনিক খেয়াল"),
            tags = listOf("newest")
        ))
    }
}
