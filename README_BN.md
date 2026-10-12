# SJ AI — Android প্রজেক্ট (প্রাথমিক সংস্করণ)

এই প্রজেক্টে সাদা UI, বাংলা ভয়েস ইনপুটের জন্য Android speech intent, এবং ফোনের Text-to-Speech ব্যবহার করা হয়েছে। এটি **পূর্ণ AI নয়**; কয়েকটি সাধারণ কমান্ডের উত্তর দেয়। “Hey SJ” সবসময় শোনা, স্ক্রিন বন্ধ অবস্থায় wake word, অনলাইন AI backend, এবং নির্দিষ্ট নারী কণ্ঠ বেছে নেওয়া এখনো এতে নেই।

## APK বানানোর সহজতম উপায়
1. এই ZIP ফাইলটি GitHub-এ নতুন repository-তে আপলোড করুন (ZIP-এর ভেতরের ফাইলগুলো repository root-এ থাকতে হবে; `SJ_AI_Android_Project_v3` ফোল্ডারসহ নয়)।
2. GitHub repository-র **Actions** ট্যাবে যান।
3. **Build SJ AI APK** workflow বেছে **Run workflow** চাপুন।
4. কাজ শেষ হলে workflow run-এর নিচের **Artifacts** থেকে `SJ-AI-debug-APK` ডাউনলোড করুন।
5. ZIP extract করে `app-debug.apk` ফোনে খুলে Android-এর নির্দেশনা অনুসারে install করুন।

## সীমাবদ্ধতা
- Speech recognition ফোনের Android/Google voice service ও ইন্টারনেট সেটিংসের ওপর নির্ভর করে।
- মাইক্রোফোন ব্যবহারের অনুমতি চাইবে।
- TTS কণ্ঠ ফোনে ইনস্টল করা voice engine-এর ওপর নির্ভরশীল।
- স্ক্রিন বন্ধ অবস্থায় “Hey SJ” সবসময় শোনা এই সংস্করণে নেই।
