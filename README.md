# FreeCodeCamp Streak Widget

A simple Android home screen widget for tracking your daily coding streak at [freeCodeCamp](https://www.freecodecamp.org/). This widget displays your streak status directly on your device's home screen, keeping you motivated to code every day!

## Features

- Connects to the public freeCodeCamp profile API to check your latest challenge completions.
- Displays whether you have completed your daily coding challenge ("DONE!" / "PENDING").
- Auto-refreshes to keep your streak info updated.

## Getting Started

1. **Clone the Repository**
   ```bash
   git clone https://github.com/<your-username>/FreeCodeCampStreak.git
   ```

2. **Import in Android Studio**
   - Open Android Studio.
   - Select `Open an Existing Project` and choose the cloned folder.

3. **Run on your device/emulator**
   - Build and run the project as usual.
   - Add the widget to your home screen and configure your freeCodeCamp username.

## To Do / Improvements

- [ ] **Add unit tests**  
  Increase test coverage, especially for API parsing and streak calculations.

- [ ] **Improve UI to display total streaks**  
  Clearly show the total number of consecutive days of coding (current streak) when the daily exercise is completed.

- [ ] **Enhance UI to show last 7 days/week summary**  
  Display a simple history of your recent daily activity, such as a 7-day streak chart or list.

- [ ] **General UI/UX improvements**  
  Make the widget more visually appealing and accessible, improve color scheme, animations, etc.

- [ ] **Widget click opens freecodecamp.org/learn**
  Add a deep link so that tapping the widget opens the freeCodeCamp "Learn" page in the browser.

## Contributing

Pull requests are welcome! If you have suggestions or want to implement one of the improvements above, feel free to open an issue or PR.

## License

This project is licensed under the MIT License.

---

Made with ❤️ by jvvppeereira 
