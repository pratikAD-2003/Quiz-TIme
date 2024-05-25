package com.example.quiz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.quiz.databinding.ActivityMainBinding;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;
    ArrayList<QuestionModel> list;
    int currentQuestionIndex = 0;
    int score;
    String Category;

    private static final String PREFS_NAME = "QuizPrefs";
    private static final String HIGH_SCORE_KEY = "HighScore";
    private static final String PAGE = "page";
    private SharedPreferences sharedPreferences;
    private int highScore;
    Handler handler;
    Runnable runnable;
    int counter = 60;

    boolean isTapOnNext = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        highScore = sharedPreferences.getInt(HIGH_SCORE_KEY, 0);

        Category = getIntent().getStringExtra("category");

        sharedPreferences = getSharedPreferences(Category, MODE_PRIVATE);
        currentQuestionIndex = sharedPreferences.getInt(PAGE, 0);

        binding.setCurrentQIndex.setText(String.valueOf(currentQuestionIndex + 1));
        loadQuestion();
        displayQuestion();
        startTime();

        binding.backFromMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
                updateHighScore();
                finish();
            }
        });

        binding.nextQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (currentQuestionIndex < list.size()) {
                    stopTimer();
                    startTime();
                    isTapOnNext = true;
                    currentQuestionIndex++;
                    binding.setCurrentQIndex.setText(String.valueOf(currentQuestionIndex + 1));
                    sharedPreferences = getSharedPreferences(Category, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(PAGE, currentQuestionIndex);
                    editor.apply();
                    resetUIBtn();
                    displayQuestion();
                    counter = 60;
                } else {
                    stopTimer();
                    isTapOnNext = false;
                    sharedPreferences = getSharedPreferences(Category, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(PAGE, 0);
                    editor.apply();
                    updateHighScore();
                    Toast.makeText(MainActivity.this, "All questions attempted of selected Category.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, UserStatus.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        binding.seeAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTimer();
                updateUIBtnIfRight(list.get(currentQuestionIndex).getAnswer());
                if (!isTapOnNext) {
                    autoNext();
                }
            }
        });
    }

    private void loadQuestion() {
        getCategory();
    }

    private void displayQuestion() {
        isTapOnNext = false;
        binding.question.setText(list.get(currentQuestionIndex).getQuestion());
        binding.optOne.setText(list.get(currentQuestionIndex).getOptions()[0]);
        binding.optTwo.setText(list.get(currentQuestionIndex).getOptions()[1]);
        binding.optThird.setText(list.get(currentQuestionIndex).getOptions()[2]);
        binding.optFour.setText(list.get(currentQuestionIndex).getOptions()[3]);
    }

    public void onSelectAnswer(View view) {
        Button clickedButton = (Button) view;
        int index = Integer.parseInt(clickedButton.getTag().toString());
        checkAnswer(index);
    }

    private void checkAnswer(int selectedBtnIndex) {

        if (selectedBtnIndex == list.get(currentQuestionIndex).getAnswer()) {
//            Log.d("CHeck", "dfda");
            stopTimer();
            score++;
            updateUIBtnIfRight(selectedBtnIndex);
            if (!isTapOnNext) {
                autoNext();
            }
        } else {
            stopTimer();
//            Log.d("wronge", "adaf");
            updateUIBtnIfWrong(selectedBtnIndex);
            updateUIBtnIfRight(list.get(currentQuestionIndex).getAnswer());
            if (!isTapOnNext) {
                autoNext();
            }
        }
    }

    private void updateUIBtnIfRight(int btnIndex) {
        switch (btnIndex) {
            case 0:
                binding.optOne.setTextColor(getResources().getColor(R.color.white));
                binding.optOne.setBackground(getDrawable(R.drawable.selected_ans_btn));
                break;
            case 1:
                binding.optTwo.setTextColor(getResources().getColor(R.color.white));
                binding.optTwo.setBackground(getDrawable(R.drawable.selected_ans_btn));
                break;
            case 2:
                binding.optThird.setTextColor(getResources().getColor(R.color.white));
                binding.optThird.setBackground(getDrawable(R.drawable.selected_ans_btn));
                break;
            case 3:
                binding.optFour.setTextColor(getResources().getColor(R.color.white));
                binding.optFour.setBackground(getDrawable(R.drawable.selected_ans_btn));
                break;
            default:
                break;
        }
    }

    private void updateUIBtnIfWrong(int btnIndex) {
        switch (btnIndex) {
            case 0:
                binding.optOne.setTextColor(getResources().getColor(R.color.white));
                binding.optOne.setBackground(getDrawable(R.drawable.wrong_answer));
                break;
            case 1:
                binding.optTwo.setTextColor(getResources().getColor(R.color.white));
                binding.optTwo.setBackground(getDrawable(R.drawable.wrong_answer));
                break;
            case 2:
                binding.optThird.setTextColor(getResources().getColor(R.color.white));
                binding.optThird.setBackground(getDrawable(R.drawable.wrong_answer));
                break;
            case 3:
                binding.optFour.setTextColor(getResources().getColor(R.color.white));
                binding.optFour.setBackground(getDrawable(R.drawable.wrong_answer));
                break;
            default:
                break;
        }
    }

    private void resetUIBtn() {
        binding.optOne.setBackground(getDrawable(R.drawable.custom_btn));
        binding.optTwo.setBackground(getDrawable(R.drawable.custom_btn));
        binding.optThird.setBackground(getDrawable(R.drawable.custom_btn));
        binding.optFour.setBackground(getDrawable(R.drawable.custom_btn));
        binding.optOne.setTextColor(getResources().getColor(R.color.black));
        binding.optTwo.setTextColor(getResources().getColor(R.color.black));
        binding.optThird.setTextColor(getResources().getColor(R.color.black));
        binding.optFour.setTextColor(getResources().getColor(R.color.black));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopTimer();
        updateHighScore();
    }

    private void updateHighScore() {
        if (score > highScore) {
            highScore = score;
            sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(HIGH_SCORE_KEY, highScore);
            editor.apply();
        }
    }

    private void startTime() {
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (counter >= 0) {
                    binding.TimePeriod.setText(String.valueOf(counter));
                    counter--;
                    handler.postDelayed(this, 1000);
                }
            }
        };

        handler.postDelayed(runnable, 1000);
    }

    private void stopTimer() {
        handler.removeCallbacks(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopTimer();
    }

    private void autoNext() {
        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (currentQuestionIndex < list.size()) {
                    stopTimer();
                    startTime();
                    currentQuestionIndex++;
                    sharedPreferences = getSharedPreferences(Category, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(PAGE, currentQuestionIndex);
                    editor.apply();
                    resetUIBtn();
                    displayQuestion();
                    counter = 60;
                    binding.setCurrentQIndex.setText(String.valueOf(currentQuestionIndex + 1));
                } else {
                    stopTimer();
                    sharedPreferences = getSharedPreferences(Category, MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putInt(PAGE, 0);
                    editor.apply();
                    updateHighScore();
                    Toast.makeText(MainActivity.this, "All questions attempted of selected Category.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, UserStatus.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);

    }

    private void getCategory() {
//        list.clear();
        switch (Category) {
            case "gk":
                if (list != null) {
                    list.clear();
                }
                binding.choosenCategory.setText("General Knowledge");
                list = new ArrayList<>();
                list.add(new QuestionModel("What is the capital of France?", new String[]{"Madrid", "Rome", "Paris", "Berlin"}, 2));
                list.add(new QuestionModel("Who wrote the play 'Romeo and Juliet'?", new String[]{"William Shakespeare", "Charles Dickens", "Mark Twain", "Jane Austen"}, 0));
                list.add(new QuestionModel("What is the largest planet in our solar system?", new String[]{"Earth", "Mars", "Jupiter", "Saturn"}, 2));
                list.add(new QuestionModel("Which element has the chemical symbol 'O'?", new String[]{"Gold", "Oxygen", "Osmium", "Potassium"}, 1));
                list.add(new QuestionModel("Who was the first President of the United States?", new String[]{"Abraham Lincoln", "George Washington", "Thomas Jefferson", "John Adams"}, 1));
                list.add(new QuestionModel("What is the smallest prime number?", new String[]{"0", "1", "2", "3"}, 2));
                list.add(new QuestionModel("Which ocean is the largest by surface area?", new String[]{"Atlantic Ocean", "Indian Ocean", "Arctic Ocean", "Pacific Ocean"}, 3));
                list.add(new QuestionModel("In which year did the Titanic sink?", new String[]{"1912", "1915", "1905", "1920"}, 0));
                list.add(new QuestionModel("What is the boiling point of water at sea level?", new String[]{"90°C", "100°C", "110°C", "120°C"}, 1));
                list.add(new QuestionModel("Who painted the Mona Lisa?", new String[]{"Vincent van Gogh", "Pablo Picasso", "Leonardo da Vinci", "Claude Monet"}, 2));
                list.add(new QuestionModel("What is the capital city of Australia?", new String[]{"Sydney", "Melbourne", "Canberra", "Brisbane"}, 2));
                list.add(new QuestionModel("Which planet is known as the Red Planet?", new String[]{"Venus", "Mars", "Saturn", "Mercury"}, 1));
                list.add(new QuestionModel("What is the hardest natural substance on Earth?", new String[]{"Gold", "Iron", "Diamond", "Platinum"}, 2));
                list.add(new QuestionModel("Who is the author of the 'Harry Potter' series?", new String[]{"J.R.R. Tolkien", "J.K. Rowling", "C.S. Lewis", "Suzanne Collins"}, 1));
                list.add(new QuestionModel("What is the largest mammal in the world?", new String[]{"Elephant", "Blue Whale", "Giraffe", "Great White Shark"}, 1));
                list.add(new QuestionModel("What is the main ingredient in traditional Japanese miso soup?", new String[]{"Chicken", "Tofu", "Beef", "Fish"}, 1));
                list.add(new QuestionModel("Which country is known as the Land of the Rising Sun?", new String[]{"China", "South Korea", "Japan", "Thailand"}, 2));
                list.add(new QuestionModel("What is the smallest country in the world by land area?", new String[]{"Monaco", "San Marino", "Liechtenstein", "Vatican City"}, 3));
                list.add(new QuestionModel("Who invented the light bulb?", new String[]{"Nikola Tesla", "Alexander Graham Bell", "Thomas Edison", "Albert Einstein"}, 2));
                list.add(new QuestionModel("What is the tallest mountain in the world?", new String[]{"K2", "Kangchenjunga", "Mount Everest", "Lhotse"}, 2));
                break;
            case "his":
                if (list != null) {
                    list.clear();
                }
                binding.choosenCategory.setText("History");
                list = new ArrayList<>();
                list.add(new QuestionModel("Who was the first President of the United States?", new String[]{"Abraham Lincoln", "George Washington", "Thomas Jefferson", "John Adams"}, 1));
                list.add(new QuestionModel("In which year did the Titanic sink?", new String[]{"1912", "1915", "1905", "1920"}, 0));
                list.add(new QuestionModel("Who wrote the play 'Romeo and Juliet'?", new String[]{"William Shakespeare", "Charles Dickens", "Mark Twain", "Jane Austen"}, 0));
                list.add(new QuestionModel("What ancient civilization built the Machu Picchu complex in Peru?", new String[]{"Maya", "Aztec", "Inca", "Olmec"}, 2));
                list.add(new QuestionModel("Who was known as the 'Mad Monk' in Russia?", new String[]{"Ivan the Terrible", "Rasputin", "Trotsky", "Lenin"}, 1));
                list.add(new QuestionModel("Which war was fought between the North and South regions in the United States?", new String[]{"World War I", "World War II", "The Civil War", "The Revolutionary War"}, 2));
                list.add(new QuestionModel("Who discovered penicillin?", new String[]{"Marie Curie", "Alexander Fleming", "Louis Pasteur", "Joseph Lister"}, 1));
                list.add(new QuestionModel("In what year did the Berlin Wall fall?", new String[]{"1987", "1989", "1991", "1993"}, 1));
                list.add(new QuestionModel("Who was the British Prime Minister during World War II?", new String[]{"Neville Chamberlain", "Winston Churchill", "Margaret Thatcher", "Clement Attlee"}, 1));
                list.add(new QuestionModel("Which Egyptian queen was known for her relationships with Julius Caesar and Mark Antony?", new String[]{"Nefertiti", "Hatshepsut", "Cleopatra", "Nefertari"}, 2));
                list.add(new QuestionModel("The Battle of Hastings was fought in which year?", new String[]{"1066", "1215", "1415", "1485"}, 0));
                list.add(new QuestionModel("Who was the longest reigning British monarch before Queen Elizabeth II?", new String[]{"Queen Victoria", "King George V", "Queen Elizabeth I", "King Henry VIII"}, 0));
                list.add(new QuestionModel("What was the name of the ship that brought the Pilgrims to America in 1620?", new String[]{"Santa Maria", "Mayflower", "Endeavour", "Discovery"}, 1));
                list.add(new QuestionModel("Who was the first emperor of China?", new String[]{"Qin Shi Huang", "Liu Bang", "Kublai Khan", "Sun Yat-sen"}, 0));
                list.add(new QuestionModel("Which country was the first to grant women the right to vote?", new String[]{"United States", "United Kingdom", "New Zealand", "Australia"}, 2));
                list.add(new QuestionModel("What was the primary cause of the Trojan War according to Greek mythology?", new String[]{"The kidnapping of Helen", "Trade disputes", "Territorial expansion", "Religious differences"}, 0));
                list.add(new QuestionModel("Who was the leader of the Soviet Union during the Cuban Missile Crisis?", new String[]{"Joseph Stalin", "Nikita Khrushchev", "Leonid Brezhnev", "Mikhail Gorbachev"}, 1));
                list.add(new QuestionModel("Which ancient Greek philosopher taught Alexander the Great?", new String[]{"Plato", "Socrates", "Aristotle", "Pythagoras"}, 2));
                list.add(new QuestionModel("What was the name of the last queen of France before the French Revolution?", new String[]{"Marie Antoinette", "Catherine de' Medici", "Eleanor of Aquitaine", "Anne of Brittany"}, 0));
                list.add(new QuestionModel("In which city was Archduke Franz Ferdinand assassinated, sparking World War I?", new String[]{"Sarajevo", "Vienna", "Belgrade", "Zagreb"}, 0));
                break;
            case "GEO":
                if (list != null) {
                    list.clear();
                }
                binding.choosenCategory.setText("Geography");
                list = new ArrayList<>();
                list.add(new QuestionModel("What is the capital of Australia?", new String[]{"Sydney", "Melbourne", "Canberra", "Brisbane"}, 2));
                list.add(new QuestionModel("Which is the longest river in the world?", new String[]{"Amazon River", "Nile River", "Yangtze River", "Mississippi River"}, 1));
                list.add(new QuestionModel("What is the largest desert in the world?", new String[]{"Sahara Desert", "Arabian Desert", "Gobi Desert", "Kalahari Desert"}, 0));
                list.add(new QuestionModel("Which country has the largest population?", new String[]{"India", "United States", "China", "Indonesia"}, 2));
                list.add(new QuestionModel("What is the smallest country in the world by land area?", new String[]{"Monaco", "San Marino", "Liechtenstein", "Vatican City"}, 3));
                list.add(new QuestionModel("Which continent is known as the 'Dark Continent'?", new String[]{"Asia", "Africa", "South America", "Europe"}, 1));
                list.add(new QuestionModel("Which ocean is the largest by surface area?", new String[]{"Atlantic Ocean", "Indian Ocean", "Arctic Ocean", "Pacific Ocean"}, 3));
                list.add(new QuestionModel("Mount Everest is located in which mountain range?", new String[]{"Andes", "Rockies", "Himalayas", "Alps"}, 2));
                list.add(new QuestionModel("What is the capital of Japan?", new String[]{"Seoul", "Beijing", "Bangkok", "Tokyo"}, 3));
                list.add(new QuestionModel("Which US state is the Grand Canyon located in?", new String[]{"California", "Arizona", "Nevada", "Utah"}, 1));
                list.add(new QuestionModel("What is the largest island in the world?", new String[]{"Borneo", "Greenland", "New Guinea", "Madagascar"}, 1));
                list.add(new QuestionModel("Which river flows through Paris?", new String[]{"Thames", "Danube", "Seine", "Rhine"}, 2));
                list.add(new QuestionModel("Which country is known as the Land of the Rising Sun?", new String[]{"China", "South Korea", "Japan", "Thailand"}, 2));
                list.add(new QuestionModel("What is the highest waterfall in the world?", new String[]{"Niagara Falls", "Victoria Falls", "Angel Falls", "Iguazu Falls"}, 2));
                list.add(new QuestionModel("Which country is the smallest by population?", new String[]{"Tuvalu", "Nauru", "Vatican City", "Monaco"}, 2));
                list.add(new QuestionModel("Which city is known as the Big Apple?", new String[]{"Los Angeles", "Chicago", "San Francisco", "New York"}, 3));
                list.add(new QuestionModel("The Great Barrier Reef is located in which country?", new String[]{"Philippines", "Fiji", "Australia", "Indonesia"}, 2));
                list.add(new QuestionModel("Which country has the most natural lakes?", new String[]{"United States", "Canada", "Russia", "Brazil"}, 1));
                list.add(new QuestionModel("What is the capital of Canada?", new String[]{"Toronto", "Vancouver", "Ottawa", "Montreal"}, 2));
                list.add(new QuestionModel("Which continent has the most countries?", new String[]{"Asia", "Africa", "Europe", "South America"}, 1));
                break;
            case "SC":
                if (list != null) {
                    list.clear();
                }
                binding.choosenCategory.setText("Science");
                list = new ArrayList<>();
                list.add(new QuestionModel("What is the chemical symbol for water?", new String[]{"H2O", "O2", "CO2", "H2"}, 0));
                list.add(new QuestionModel("Which planet is known as the Red Planet?", new String[]{"Earth", "Venus", "Mars", "Jupiter"}, 2));
                list.add(new QuestionModel("What is the hardest natural substance on Earth?", new String[]{"Gold", "Iron", "Diamond", "Silver"}, 2));
                list.add(new QuestionModel("What gas do plants absorb from the atmosphere?", new String[]{"Oxygen", "Nitrogen", "Carbon Dioxide", "Hydrogen"}, 2));
                list.add(new QuestionModel("What is the center of an atom called?", new String[]{"Electron", "Proton", "Neutron", "Nucleus"}, 3));
                list.add(new QuestionModel("How many bones are in the adult human body?", new String[]{"206", "210", "201", "215"}, 0));
                list.add(new QuestionModel("What planet is known for its rings?", new String[]{"Mars", "Jupiter", "Saturn", "Uranus"}, 2));
                list.add(new QuestionModel("What is the boiling point of water at sea level?", new String[]{"50°C", "75°C", "100°C", "125°C"}, 2));
                list.add(new QuestionModel("Which element has the chemical symbol 'Fe'?", new String[]{"Gold", "Iron", "Silver", "Lead"}, 1));
                list.add(new QuestionModel("What is the main gas found in the air we breathe?", new String[]{"Oxygen", "Hydrogen", "Carbon Dioxide", "Nitrogen"}, 3));
                list.add(new QuestionModel("What force keeps us on the ground?", new String[]{"Magnetism", "Gravity", "Friction", "Electricity"}, 1));
                list.add(new QuestionModel("What is the process by which plants make their food?", new String[]{"Photosynthesis", "Respiration", "Digestion", "Excretion"}, 0));
                list.add(new QuestionModel("What type of animal is a dolphin?", new String[]{"Fish", "Amphibian", "Mammal", "Reptile"}, 2));
                list.add(new QuestionModel("What part of the cell contains the genetic material?", new String[]{"Cytoplasm", "Nucleus", "Cell membrane", "Ribosome"}, 1));
                list.add(new QuestionModel("What planet is closest to the sun?", new String[]{"Earth", "Venus", "Mercury", "Mars"}, 2));
                list.add(new QuestionModel("What is the most abundant gas in the Earth's atmosphere?", new String[]{"Oxygen", "Carbon Dioxide", "Nitrogen", "Helium"}, 2));
                list.add(new QuestionModel("How long does it take for the Earth to orbit the sun?", new String[]{"24 hours", "365 days", "30 days", "1000 days"}, 1));
                list.add(new QuestionModel("What organ pumps blood through the body?", new String[]{"Brain", "Liver", "Heart", "Lungs"}, 2));
                list.add(new QuestionModel("What is the basic unit of life?", new String[]{"Atom", "Molecule", "Cell", "Organ"}, 2));
                list.add(new QuestionModel("What is the chemical formula for table salt?", new String[]{"H2O", "CO2", "NaCl", "C6H12O6"}, 2));
                break;
            case "LIT":
                if (list != null) {
                    list.clear();
                }
                binding.choosenCategory.setText("Literature");
                list = new ArrayList<>();
                list.add(new QuestionModel("Who wrote 'Pride and Prejudice'?", new String[]{"Charlotte Brontë", "Emily Brontë", "Jane Austen", "Mary Shelley"}, 2));
                list.add(new QuestionModel("What is the name of the hobbit played by Elijah Wood in the 'Lord of the Rings' movies?", new String[]{"Frodo Baggins", "Bilbo Baggins", "Samwise Gamgee", "Peregrin Took"}, 0));
                list.add(new QuestionModel("Which Shakespeare play features the characters of Rosencrantz and Guildenstern?", new String[]{"Macbeth", "Hamlet", "Othello", "King Lear"}, 1));
                list.add(new QuestionModel("Who wrote 'To Kill a Mockingbird'?", new String[]{"Harper Lee", "Mark Twain", "F. Scott Fitzgerald", "Ernest Hemingway"}, 0));
                list.add(new QuestionModel("In 'Moby Dick', what is the name of the ship?", new String[]{"Pequod", "Beagle", "Hispaniola", "Nautilus"}, 0));
                list.add(new QuestionModel("Which novel begins with the line 'Call me Ishmael'?", new String[]{"Moby Dick", "Pride and Prejudice", "The Great Gatsby", "War and Peace"}, 0));
                list.add(new QuestionModel("Who is the author of the 'Harry Potter' series?", new String[]{"J.R.R. Tolkien", "J.K. Rowling", "C.S. Lewis", "Philip Pullman"}, 1));
                list.add(new QuestionModel("In George Orwell's '1984', what is the name of the totalitarian regime's leader?", new String[]{"Big Brother", "Old Major", "Comrade Napoleon", "The Party Leader"}, 0));
                list.add(new QuestionModel("Who wrote 'The Catcher in the Rye'?", new String[]{"J.D. Salinger", "Ernest Hemingway", "F. Scott Fitzgerald", "John Steinbeck"}, 0));
                list.add(new QuestionModel("What is the title of the first book in the 'A Song of Ice and Fire' series by George R.R. Martin?", new String[]{"A Clash of Kings", "A Storm of Swords", "A Game of Thrones", "A Feast for Crows"}, 2));
                list.add(new QuestionModel("Who wrote 'The Great Gatsby'?", new String[]{"Ernest Hemingway", "F. Scott Fitzgerald", "John Steinbeck", "Mark Twain"}, 1));
                list.add(new QuestionModel("In which novel would you find the character Atticus Finch?", new String[]{"The Grapes of Wrath", "To Kill a Mockingbird", "Of Mice and Men", "The Catcher in the Rye"}, 1));
                list.add(new QuestionModel("Who wrote 'Jane Eyre'?", new String[]{"Jane Austen", "Mary Shelley", "Emily Brontë", "Charlotte Brontë"}, 3));
                list.add(new QuestionModel("What is the name of the fictional town where the events of 'To Kill a Mockingbird' take place?", new String[]{"Maycomb", "Macondo", "Middlemarch", "Manderley"}, 0));
                list.add(new QuestionModel("Who is the author of 'The Hobbit'?", new String[]{"J.K. Rowling", "C.S. Lewis", "George R.R. Martin", "J.R.R. Tolkien"}, 3));
                list.add(new QuestionModel("Which novel features the character of Holden Caulfield?", new String[]{"The Great Gatsby", "The Catcher in the Rye", "To Kill a Mockingbird", "Lord of the Flies"}, 1));
                list.add(new QuestionModel("Who wrote 'Brave New World'?", new String[]{"Aldous Huxley", "George Orwell", "Ray Bradbury", "Philip K. Dick"}, 0));
                list.add(new QuestionModel("What is the first name of the main character in 'The Hunger Games'?", new String[]{"Katniss", "Hermione", "Bella", "Tris"}, 0));
                list.add(new QuestionModel("Who wrote 'The Chronicles of Narnia'?", new String[]{"J.R.R. Tolkien", "J.K. Rowling", "C.S. Lewis", "Philip Pullman"}, 2));
                list.add(new QuestionModel("Which famous detective is featured in novels by Arthur Conan Doyle?", new String[]{"Hercule Poirot", "Sam Spade", "Sherlock Holmes", "Philip Marlowe"}, 2));
                break;
            case "SPT":
                if (list != null) {
                    list.clear();
                }
                binding.choosenCategory.setText("Sports");
                list = new ArrayList<>();
                list.add(new QuestionModel("Which country won the 2018 FIFA World Cup?", new String[]{"France", "Croatia", "Belgium", "Brazil"}, 0));
                list.add(new QuestionModel("Who won the 2019 Wimbledon Men's Singles title?", new String[]{"Rafael Nadal", "Roger Federer", "Novak Djokovic", "Andy Murray"}, 2));
                list.add(new QuestionModel("Which team has won the most NBA championships?", new String[]{"Los Angeles Lakers", "Chicago Bulls", "Boston Celtics", "Golden State Warriors"}, 2));
                list.add(new QuestionModel("Who holds the record for the most Olympic gold medals?", new String[]{"Michael Phelps", "Usain Bolt", "Carl Lewis", "Larisa Latynina"}, 0));
                list.add(new QuestionModel("Which city hosted the 2016 Summer Olympics?", new String[]{"London", "Beijing", "Rio de Janeiro", "Tokyo"}, 2));
                list.add(new QuestionModel("Who is the all-time leading goal scorer in the history of the FIFA World Cup?", new String[]{"Lionel Messi", "Cristiano Ronaldo", "Pele", "Miroslav Klose"}, 3));
                list.add(new QuestionModel("Which country has won the most Cricket World Cups?", new String[]{"Australia", "India", "West Indies", "England"}, 0));
                list.add(new QuestionModel("Who is the only player to have won the FIFA Ballon d'Or five times?", new String[]{"Lionel Messi", "Cristiano Ronaldo", "Diego Maradona", "Pelé"}, 0));
                list.add(new QuestionModel("In which sport would you perform a 'slam dunk'?", new String[]{"Soccer", "Tennis", "Basketball", "Golf"}, 2));
                list.add(new QuestionModel("Which country won the most medals in the 2016 Summer Olympics?", new String[]{"United States", "China", "Russia", "Great Britain"}, 0));
                list.add(new QuestionModel("Who holds the record for the fastest 100-meter sprint?", new String[]{"Usain Bolt", "Carl Lewis", "Asafa Powell", "Justin Gatlin"}, 0));
                list.add(new QuestionModel("What is the highest score possible in a game of bowling?", new String[]{"200", "250", "300", "350"}, 2));
                list.add(new QuestionModel("Which boxer was known as 'The Greatest'?", new String[]{"Mike Tyson", "Muhammad Ali", "Floyd Mayweather Jr.", "Sugar Ray Robinson"}, 1));
                list.add(new QuestionModel("Who is the youngest Formula One World Champion?", new String[]{"Michael Schumacher", "Sebastian Vettel", "Lewis Hamilton", "Fernando Alonso"}, 1));
                list.add(new QuestionModel("In which city are the headquarters of the International Olympic Committee located?", new String[]{"Lausanne", "Geneva", "Zurich", "Bern"}, 0));
                list.add(new QuestionModel("Who holds the record for the most goals scored in a single NHL season?", new String[]{"Wayne Gretzky", "Mario Lemieux", "Bobby Orr", "Gordie Howe"}, 0));
                list.add(new QuestionModel("Which athlete was known as 'The Flying Finn'?", new String[]{"Jesse Owens", "Paavo Nurmi", "Emil Zátopek", "Carl Lewis"}, 1));
                list.add(new QuestionModel("Who won the 2019 FIFA Women's World Cup?", new String[]{"United States", "Netherlands", "Sweden", "Germany"}, 0));
                list.add(new QuestionModel("Which tennis player has won the most Grand Slam titles in history?", new String[]{"Serena Williams", "Margaret Court", "Steffi Graf", "Martina Navratilova"}, 1));
                list.add(new QuestionModel("Which team won the first ever Super Bowl?", new String[]{"Green Bay Packers", "Kansas City Chiefs", "Pittsburgh Steelers", "Dallas Cowboys"}, 0));
                break;
            case "ETR":
                if (list != null) {
                    list.clear();
                }
                binding.choosenCategory.setText("Entertainment");
                list = new ArrayList<>();
                list.add(new QuestionModel("Who played the role of Tony Stark in the Marvel Cinematic Universe?", new String[]{"Chris Hemsworth", "Chris Evans", "Robert Downey Jr.", "Mark Ruffalo"}, 2));
                list.add(new QuestionModel("Which animated film features a young lion named Simba?", new String[]{"Toy Story", "Finding Nemo", "The Lion King", "Shrek"}, 2));
                list.add(new QuestionModel("Who wrote the 'Harry Potter' book series?", new String[]{"J.R.R. Tolkien", "J.K. Rowling", "C.S. Lewis", "Stephen King"}, 1));
                list.add(new QuestionModel("Which actor played the character of Neo in 'The Matrix' trilogy?", new String[]{"Tom Cruise", "Keanu Reeves", "Brad Pitt", "Leonardo DiCaprio"}, 1));
                list.add(new QuestionModel("Which TV series features characters such as Ross, Rachel, and Joey?", new String[]{"Breaking Bad", "Friends", "The Office", "Game of Thrones"}, 1));
                list.add(new QuestionModel("Who directed the movie 'Inception'?", new String[]{"Steven Spielberg", "Quentin Tarantino", "Christopher Nolan", "Martin Scorsese"}, 2));
                list.add(new QuestionModel("Which fictional character lives in a pineapple under the sea?", new String[]{"SpongeBob SquarePants", "Bugs Bunny", "Mickey Mouse", "Homer Simpson"}, 0));
                list.add(new QuestionModel("Which actress portrays Katniss Everdeen in 'The Hunger Games' film series?", new String[]{"Emma Stone", "Jennifer Lawrence", "Anne Hathaway", "Emma Watson"}, 1));
                list.add(new QuestionModel("Which band is known for the hit song 'Bohemian Rhapsody'?", new String[]{"The Beatles", "Queen", "Led Zeppelin", "Pink Floyd"}, 1));
                list.add(new QuestionModel("Which film won the Academy Award for Best Picture in 2020?", new String[]{"Parasite", "Joker", "1917", "Once Upon a Time in Hollywood"}, 0));
                list.add(new QuestionModel("Who voiced the character of Woody in the 'Toy Story' film series?", new String[]{"Tom Hanks", "Tim Allen", "Will Smith", "Robin Williams"}, 0));
                list.add(new QuestionModel("What is the name of the wizard in 'The Lord of the Rings' who guides Frodo and the Fellowship?", new String[]{"Gandalf", "Saruman", "Radagast", "Elrond"}, 0));
                list.add(new QuestionModel("Who played the character of Jack Dawson in the movie 'Titanic'?", new String[]{"Leonardo DiCaprio", "Brad Pitt", "Tom Cruise", "Johnny Depp"}, 0));
                list.add(new QuestionModel("Which actress played the role of Hermione Granger in the 'Harry Potter' film series?", new String[]{"Emma Watson", "Emma Stone", "Jennifer Lawrence", "Dakota Fanning"}, 0));
                list.add(new QuestionModel("What is the name of Batman's butler?", new String[]{"Alfred", "Jeeves", "Walter", "Giles"}, 0));
                list.add(new QuestionModel("Who directed the movie 'Pulp Fiction'?", new String[]{"Martin Scorsese", "Quentin Tarantino", "Steven Spielberg", "Christopher Nolan"}, 1));
                list.add(new QuestionModel("Which TV series features characters like Walter White and Jesse Pinkman?", new String[]{"Breaking Bad", "The Sopranos", "The Wire", "Mad Men"}, 0));
                list.add(new QuestionModel("Which movie features a character named Forrest Gump?", new String[]{"The Shawshank Redemption", "Forrest Gump", "The Green Mile", "Goodfellas"}, 1));
                list.add(new QuestionModel("Who played the role of Captain Jack Sparrow in the 'Pirates of the Caribbean' film series?", new String[]{"Tom Hanks", "Johnny Depp", "Brad Pitt", "Robert Downey Jr."}, 1));
                list.add(new QuestionModel("Which TV series is set in the fictional continent of Westeros?", new String[]{"Breaking Bad", "Friends", "Game of Thrones", "Stranger Things"}, 2));
                break;
            case "music":
                if (list != null) {
                    list.clear();
                }
                binding.choosenCategory.setText("Music");
                list = new ArrayList<>();
                list.add(new QuestionModel("Which band released the album 'Abbey Road'?", new String[]{"The Beatles", "Led Zeppelin", "The Rolling Stones", "Queen"}, 0));
                list.add(new QuestionModel("Who is known as the 'King of Pop'?", new String[]{"Michael Jackson", "Elvis Presley", "Prince", "David Bowie"}, 0));
                list.add(new QuestionModel("What instrument does a percussionist play?", new String[]{"Guitar", "Drums", "Violin", "Piano"}, 1));
                list.add(new QuestionModel("Which rapper released the album 'The Marshall Mathers LP'?", new String[]{"Jay-Z", "Eminem", "Kanye West", "Drake"}, 1));
                list.add(new QuestionModel("Who is the lead singer of the band 'Queen'?", new String[]{"Freddie Mercury", "Roger Waters", "David Gilmour", "Robert Plant"}, 0));
                list.add(new QuestionModel("Which genre of music originated in Jamaica in the late 1960s?", new String[]{"Jazz", "Reggae", "Hip Hop", "Salsa"}, 1));
                list.add(new QuestionModel("Which female artist released the album '21' in 2011?", new String[]{"Adele", "Beyoncé", "Taylor Swift", "Rihanna"}, 0));
                list.add(new QuestionModel("Who composed the 'Four Seasons'?", new String[]{"Johann Sebastian Bach", "Wolfgang Amadeus Mozart", "Ludwig van Beethoven", "Antonio Vivaldi"}, 3));
                list.add(new QuestionModel("Which rock band released the album 'Dark Side of the Moon'?", new String[]{"The Rolling Stones", "Pink Floyd", "Led Zeppelin", "The Who"}, 1));
                list.add(new QuestionModel("Who won the first season of 'American Idol'?", new String[]{"Kelly Clarkson", "Adam Lambert", "Carrie Underwood", "Jennifer Hudson"}, 0));
                list.add(new QuestionModel("Which music artist is known for their song 'Purple Rain'?", new String[]{"Prince", "Michael Jackson", "David Bowie", "Elvis Presley"}, 0));
                list.add(new QuestionModel("What is the highest-pitched woodwind instrument in the orchestra?", new String[]{"Flute", "Clarinet", "Oboe", "Piccolo"}, 3));
                list.add(new QuestionModel("Who wrote the musical 'Les Misérables'?", new String[]{"Andrew Lloyd Webber", "Stephen Sondheim", "Richard Rodgers", "Claude-Michel Schönberg"}, 3));
                list.add(new QuestionModel("Which pop artist released the album '1989'?", new String[]{"Taylor Swift", "Ariana Grande", "Katy Perry", "Lady Gaga"}, 0));
                list.add(new QuestionModel("Which country is famous for the traditional music style known as flamenco?", new String[]{"Spain", "Italy", "France", "Portugal"}, 0));
                list.add(new QuestionModel("Who is the guitarist for the band 'The Rolling Stones'?", new String[]{"Jimmy Page", "Keith Richards", "Eric Clapton", "Jimi Hendrix"}, 1));
                list.add(new QuestionModel("Which composer is known for his 'Ode to Joy'?", new String[]{"Wolfgang Amadeus Mozart", "Johann Sebastian Bach", "Ludwig van Beethoven", "Franz Schubert"}, 2));
                list.add(new QuestionModel("Which pop group performed the song 'Waterloo'?", new String[]{"ABBA", "The Beatles", "The Bee Gees", "The Beach Boys"}, 0));
                list.add(new QuestionModel("What is the name of Beyoncé's alter ego?", new String[]{"Sasha Fierce", "Queen Bey", "Bee Hive", "Yoncé"}, 0));
                list.add(new QuestionModel("Which musical features the song 'Memory'?", new String[]{"Cats", "Les Misérables", "Phantom of the Opera", "West Side Story"}, 0));
                break;
            case "movies":
                if (list != null) {
                    list.clear();
                }
                binding.choosenCategory.setText("Movies");
                list = new ArrayList<>();
                list.add(new QuestionModel("Who directed the movie 'The Godfather'?", new String[]{"Martin Scorsese", "Francis Ford Coppola", "Steven Spielberg", "Alfred Hitchcock"}, 1));
                list.add(new QuestionModel("Which film won the Academy Award for Best Picture in 1994?", new String[]{"Forrest Gump", "Schindler's List", "The Shawshank Redemption", "Pulp Fiction"}, 0));
                list.add(new QuestionModel("Who played the character of Jack Dawson in the movie 'Titanic'?", new String[]{"Leonardo DiCaprio", "Brad Pitt", "Tom Cruise", "Johnny Depp"}, 0));
                list.add(new QuestionModel("Which movie features the character of Hannibal Lecter?", new String[]{"The Silence of the Lambs", "Se7en", "Psycho", "American Psycho"}, 0));
                list.add(new QuestionModel("Who won the Academy Award for Best Actor for his role in the movie 'The Revenant'?", new String[]{"Johnny Depp", "Brad Pitt", "Leonardo DiCaprio", "Tom Hanks"}, 2));
                list.add(new QuestionModel("What is the highest-grossing film of all time?", new String[]{"Avatar", "Avengers: Endgame", "Titanic", "Star Wars: The Force Awakens"}, 1));
                list.add(new QuestionModel("Which movie features a character named Tony Stark?", new String[]{"Iron Man", "Spider-Man: Homecoming", "Batman Begins", "X-Men"}, 0));
                list.add(new QuestionModel("Who directed the movie 'Jurassic Park'?", new String[]{"James Cameron", "Christopher Nolan", "Steven Spielberg", "George Lucas"}, 2));
                list.add(new QuestionModel("Which film won the Academy Award for Best Animated Feature in 2001?", new String[]{"Finding Nemo", "Shrek", "Spirited Away", "Toy Story 2"}, 2));
                list.add(new QuestionModel("Who played the character of Ellen Ripley in the 'Alien' film series?", new String[]{"Sigourney Weaver", "Meryl Streep", "Angelina Jolie", "Michelle Pfeiffer"}, 0));
                list.add(new QuestionModel("Which movie features the character of Neo?", new String[]{"The Matrix", "Blade Runner", "Inception", "Interstellar"}, 0));
                list.add(new QuestionModel("Who directed the movie 'Schindler's List'?", new String[]{"Steven Spielberg", "Martin Scorsese", "Quentin Tarantino", "Francis Ford Coppola"}, 0));
                list.add(new QuestionModel("Which film won the Academy Award for Best Picture in 2016?", new String[]{"Moonlight", "La La Land", "The Shape of Water", "Birdman"}, 0));
                list.add(new QuestionModel("Who played the character of Vito Corleone in 'The Godfather'?", new String[]{"Robert De Niro", "Al Pacino", "Marlon Brando", "Joe Pesci"}, 2));
                list.add(new QuestionModel("Which movie is based on a novel by Stephen King?", new String[]{"The Shawshank Redemption", "Forrest Gump", "Fight Club", "Goodfellas"}, 0));
                list.add(new QuestionModel("Who directed the movie 'Fight Club'?", new String[]{"David Fincher", "Quentin Tarantino", "Martin Scorsese", "Christopher Nolan"}, 0));
                list.add(new QuestionModel("Which actor portrayed the character of Joker in 'The Dark Knight'?", new String[]{"Heath Ledger", "Jack Nicholson", "Joaquin Phoenix", "Jared Leto"}, 0));
                list.add(new QuestionModel("Which film features the character of Norman Bates?", new String[]{"Psycho", "The Shining", "Rosemary's Baby", "The Exorcist"}, 0));
                list.add(new QuestionModel("Who won the Academy Award for Best Actress for her role in the movie 'La La Land'?", new String[]{"Emma Stone", "Meryl Streep", "Jennifer Lawrence", "Cate Blanchett"}, 0));
                list.add(new QuestionModel("Which movie features the character of Maximus Decimus Meridius?", new String[]{"Gladiator", "300", "Troy", "Braveheart"}, 0));
                break;
            case "tv":
                if (list != null) {
                    list.clear();
                }
                binding.choosenCategory.setText("TV Shows");
                list = new ArrayList<>();
                list.add(new QuestionModel("Which TV series features characters such as Ross, Rachel, and Joey?", new String[]{"Breaking Bad", "Friends", "The Office", "Game of Thrones"}, 1));
                list.add(new QuestionModel("Which TV show is set in the fictional town of Hawkins?", new String[]{"Stranger Things", "The Walking Dead", "Lost", "Westworld"}, 0));
                list.add(new QuestionModel("Who is the creator of 'The Simpsons'?", new String[]{"Matt Groening", "Seth MacFarlane", "Trey Parker", "Mike Judge"}, 0));
                list.add(new QuestionModel("Which actor portrays Sherlock Holmes in the BBC series 'Sherlock'?", new String[]{"Benedict Cumberbatch", "Robert Downey Jr.", "Johnny Lee Miller", "Jude Law"}, 0));
                list.add(new QuestionModel("Which TV series features a character named Walter White?", new String[]{"Breaking Bad", "Stranger Things", "The Sopranos", "The Wire"}, 0));
                list.add(new QuestionModel("Which TV show is set in the fictional town of Springfield?", new String[]{"Family Guy", "Bob's Burgers", "American Dad!", "The Simpsons"}, 3));
                list.add(new QuestionModel("Who is the host of the TV show 'Jeopardy!'?", new String[]{"Alex Trebek", "Pat Sajak", "Vanna White", "Regis Philbin"}, 0));
                list.add(new QuestionModel("Which TV series follows the lives of the employees of the Dunder Mifflin Paper Company?", new String[]{"Friends", "The Office", "Parks and Recreation", "Brooklyn Nine-Nine"}, 1));
                list.add(new QuestionModel("Which TV show is based on the book series by George R.R. Martin?", new String[]{"The Walking Dead", "Stranger Things", "Game of Thrones", "Westworld"}, 2));
                list.add(new QuestionModel("Who played the character of Tony Soprano in the TV series 'The Sopranos'?", new String[]{"James Gandolfini", "Al Pacino", "Robert De Niro", "Joe Pesci"}, 0));
                list.add(new QuestionModel("Which TV series follows the adventures of Rick Sanchez and his grandson Morty?", new String[]{"BoJack Horseman", "Archer", "Rick and Morty", "South Park"}, 2));
                list.add(new QuestionModel("Who created the TV series 'Breaking Bad'?", new String[]{"Vince Gilligan", "David Chase", "Matthew Weiner", "Joss Whedon"}, 0));
                list.add(new QuestionModel("Which TV show is set in the fictional town of Pawnee?", new String[]{"Brooklyn Nine-Nine", "The Office", "Parks and Recreation", "Community"}, 2));
                list.add(new QuestionModel("Who is the protagonist of the TV series 'Dexter'?", new String[]{"Dexter Morgan", "Walter White", "Tony Soprano", "Don Draper"}, 0));
                list.add(new QuestionModel("Which TV series features a character named Michael Scott?", new String[]{"The Office", "Friends", "Parks and Recreation", "Brooklyn Nine-Nine"}, 0));
                list.add(new QuestionModel("Which TV show is set in the fictional town of Dillon?", new String[]{"Friday Night Lights", "One Tree Hill", "Gilmore Girls", "Veronica Mars"}, 0));
                list.add(new QuestionModel("Who plays the character of Eleven in the TV series 'Stranger Things'?", new String[]{"Millie Bobby Brown", "Winona Ryder", "Natalia Dyer", "Sadie Sink"}, 0));
                list.add(new QuestionModel("Which TV series features a character named Don Draper?", new String[]{"Mad Men", "Breaking Bad", "The Sopranos", "The Wire"}, 0));
                list.add(new QuestionModel("Who is the creator of the TV series 'The Office'?", new String[]{"Greg Daniels", "Ricky Gervais", "Steve Carell", "Stephen Merchant"}, 0));
                list.add(new QuestionModel("Which TV show is set in the fictional town of Sunnydale?", new String[]{"Buffy the Vampire Slayer", "Angel", "Charmed", "Supernatural"}, 0));
                break;
            case "c":
                if (list != null) {
                    list.clear();
                }
                binding.choosenCategory.setText("C Language");
                list = new ArrayList<>();
                list.add(new QuestionModel("Which of the following is not a valid C++ variable name?", new String[]{"int number;", "float rate;", "int variable_count;", "float 3value;"}, 3));
                list.add(new QuestionModel("Which of the following is the correct way to declare a pointer in C++?", new String[]{"int* ptr;", "int ptr*;", "int * ptr;", "ptr int*;"}, 0));
                list.add(new QuestionModel("Which of the following correctly declares an array in C++?", new String[]{"int array[10];", "int array;", "array{10};", "int array = new int[10];"}, 0));
                list.add(new QuestionModel("What is the output of the following code?\n\n#include <iostream>\nusing namespace std;\nint main() {\n  int x = 5;\n  cout << x;\n  return 0;\n}", new String[]{"5", "x", "0", "None of the above"}, 0));
                list.add(new QuestionModel("Which of the following is used to create an object in C++?", new String[]{"new", "malloc", "alloc", "create"}, 0));
                list.add(new QuestionModel("Which operator is used to access the members of a class in C++?", new String[]{".", "->", ":", "::"}, 0));
                list.add(new QuestionModel("What is the size of an int data type in C++?", new String[]{"2 bytes", "4 bytes", "8 bytes", "Depends on the system/compiler"}, 3));
                list.add(new QuestionModel("Which of the following is used for dynamic memory allocation in C++?", new String[]{"malloc", "alloc", "new", "create"}, 2));
                list.add(new QuestionModel("What is the output of the following code?\n\n#include <iostream>\nusing namespace std;\nint main() {\n  int arr[] = {1, 2, 3, 4};\n  cout << arr[2];\n  return 0;\n}", new String[]{"1", "2", "3", "4"}, 2));
                list.add(new QuestionModel("Which keyword is used to define a constant variable in C++?", new String[]{"constant", "final", "const", "define"}, 2));
                list.add(new QuestionModel("Which of the following is the correct syntax to include a user-defined header file in C++?", new String[]{"#include <filename>", "#include \"filename\"", "#include <filename.h>", "#include \"filename.h\""}, 3));
                list.add(new QuestionModel("What will be the output of the following code?\n\n#include <iostream>\nusing namespace std;\nint main() {\n  int x = 10;\n  if (x > 0) {\n    cout << \"Positive\";\n  }\n  return 0;\n}", new String[]{"Positive", "Negative", "Zero", "None of the above"}, 0));
                list.add(new QuestionModel("Which keyword is used to come out of a loop in C++?", new String[]{"exit", "stop", "break", "return"}, 2));
                list.add(new QuestionModel("What is the value of 'x' after the following code executes?\n\nint x = 10;\nint y = x++;\n", new String[]{"10", "11", "10.5", "None of the above"}, 1));
                list.add(new QuestionModel("Which function is used to compare two strings in C++?", new String[]{"strcmp()", "strcompare()", "compare()", "compstr()"}, 2));
                list.add(new QuestionModel("What will be the output of the following code?\n\n#include <iostream>\nusing namespace std;\n#define PI 3.14\nint main() {\n  cout << PI;\n  return 0;\n}", new String[]{"3", "3.14", "PI", "None of the above"}, 1));
                list.add(new QuestionModel("Which of the following is the correct syntax to create a single-line comment in C++?", new String[]{"/* Comment */", "# Comment", "// Comment", "<!-- Comment -->"}, 2));
                list.add(new QuestionModel("What will be the output of the following code?\n\n#include <iostream>\nusing namespace std;\nint main() {\n  int a = 5, b = 3;\n  cout << a + b;\n  return 0;\n}", new String[]{"5", "3", "8", "None of the above"}, 2));
                list.add(new QuestionModel("Which of the following is used to terminate a C++ program?", new String[]{"break", "exit", "stop", "terminate"}, 1));
                list.add(new QuestionModel("What is the correct syntax to declare a class in C++?", new String[]{"class MyClass {}", "class MyClass[]", "class MyClass;", "class: MyClass {};"}, 0));
                break;
            case "ja":
                if (list != null) {
                    list.clear();
                }
                binding.choosenCategory.setText("Java");
                list = new ArrayList<>();
                list.add(new QuestionModel("What is the correct way to declare a variable in Java?", new String[]{"variable int x;", "int x;", "x = int;", "declare int x;"}, 1));
                list.add(new QuestionModel("Which of the following is NOT a primitive data type in Java?", new String[]{"int", "double", "String", "boolean"}, 2));
                list.add(new QuestionModel("What is the output of the following code?\n\nint x = 5;\nSystem.out.println(x++);", new String[]{"5", "6", "4", "0"}, 0));
                list.add(new QuestionModel("What keyword is used to define a constant in Java?", new String[]{"constant", "final", "static", "public"}, 1));
                list.add(new QuestionModel("What is the superclass of all classes in Java?", new String[]{"Object", "Super", "Main", "Root"}, 0));
                list.add(new QuestionModel("Which keyword is used to create a new instance of a class in Java?", new String[]{"new", "create", "instance", "Object"}, 0));
                list.add(new QuestionModel("Which of the following statements is used to stop the execution of a loop in Java?", new String[]{"break", "exit", "terminate", "end"}, 0));
                list.add(new QuestionModel("What is the correct syntax to declare a method in Java?", new String[]{"method int myMethod() {}", "int myMethod() {}", "void myMethod() {}", "function void myMethod() {}"}, 2));
                list.add(new QuestionModel("Which collection class allows you to access its elements by their index?", new String[]{"Set", "Map", "Array", "List"}, 3));
                list.add(new QuestionModel("What is the output of the following code?\n\nString str = \"Hello World!\";\nSystem.out.println(str.substring(6));", new String[]{"Hello", "World!", "Hello World", "World"}, 1));
                list.add(new QuestionModel("What does the 'static' keyword mean in Java?", new String[]{"The variable or method belongs to the class, rather than instances of the class", "The variable or method can be accessed from other classes", "The variable or method cannot be changed", "The variable or method is local to the class"}, 0));
                list.add(new QuestionModel("Which of the following is the correct way to initialize an array in Java?", new String[]{"int[] numbers = new int[5];", "int numbers[] = {1, 2, 3, 4, 5};", "int numbers[] = new int[5];", "All of the above"}, 3));
                list.add(new QuestionModel("What is the output of the following code?\n\nint x = 10;\nif (x > 5) {\n  System.out.println(\"x is greater than 5\");\n} else {\n  System.out.println(\"x is less than or equal to 5\");\n}", new String[]{"x is greater than 5", "x is less than or equal to 5", "10", "5"}, 0));
                list.add(new QuestionModel("Which of the following is a loop in Java?", new String[]{"for", "if", "while", "switch"}, 2));
                list.add(new QuestionModel("What is the purpose of the 'this' keyword in Java?", new String[]{"To refer to the current object", "To create a new object", "To access static members", "To call a method"}, 0));
                list.add(new QuestionModel("What will be the output of the following code?\n\nString str1 = \"Hello\";\nString str2 = \"Hello\";\nSystem.out.println(str1.equals(str2));", new String[]{"true", "false", "Error", "null"}, 0));
                list.add(new QuestionModel("Which of the following is used to read input from the user in Java?", new String[]{"System.in", "System.out", "System.err", "Scanner"}, 3));
                list.add(new QuestionModel("What is the correct syntax to declare a constructor in Java?", new String[]{"constructor MyClass() {}", "MyClass() {}", "void MyClass() {}", "MyClass:Constructor() {}"}, 1));
                list.add(new QuestionModel("Which keyword is used to define a subclass in Java?", new String[]{"class", "superclass", "extends", "implements"}, 2));
                list.add(new QuestionModel("What is the output of the following code?\n\nint x = 5;\nSystem.out.println(x--);", new String[]{"5", "6", "4", "0"}, 0));
                break;
            case "py":
                if (list != null) {
                    list.clear();
                }
                binding.choosenCategory.setText("Python");
                list = new ArrayList<>();
                list.add(new QuestionModel("What is the output of the following code?\n\nx = 5\nprint(x)", new String[]{"5", "x", "0", "None of the above"}, 0));
                list.add(new QuestionModel("Which of the following is used to comment out a single line in Python?", new String[]{"#", "//", "/*", "*/"}, 0));
                list.add(new QuestionModel("What is the correct way to declare a variable in Python?", new String[]{"variable x = 5", "x = 5", "int x = 5", "$x = 5"}, 1));
                list.add(new QuestionModel("What is the output of the following code?\n\nx = 5\ny = 3\nprint(x + y)", new String[]{"8", "15", "53", "None of the above"}, 0));
                list.add(new QuestionModel("What is the correct syntax to create a function in Python?", new String[]{"function myFunction():", "def myFunction():", "method myFunction():", "myFunction = ()"}, 1));
                list.add(new QuestionModel("Which keyword is used to define a constant in Python?", new String[]{"constant", "const", "final", "None"}, 3));
                list.add(new QuestionModel("What is the correct way to check if a variable is of a certain type in Python?", new String[]{"typeOf", "typeof", "type", "instanceOf"}, 2));
                list.add(new QuestionModel("What does the 'elif' keyword signify in Python?", new String[]{"Else if", "Else loop", "Else loop if", "End if"}, 0));
                list.add(new QuestionModel("Which of the following is the correct way to define a list in Python?", new String[]{"list = [1, 2, 3]", "{1, 2, 3}", "(1, 2, 3)", "[1, 2, 3,]"}, 0));
                list.add(new QuestionModel("What is the output of the following code?\n\nstr = 'Hello'\nprint(str[0])", new String[]{"Hello", "H", "'H'", "None"}, 1));
                list.add(new QuestionModel("What is the output of the following code?\n\nfor i in range(3):\n    print(i)", new String[]{"0 1 2", "1 2 3", "0 1 2 3", "None of the above"}, 0));
                list.add(new QuestionModel("Which of the following is used to indicate the end of a block of code in Python?", new String[]{"{} braces", "[] brackets", "() parentheses", "Indentation"}, 3));
                list.add(new QuestionModel("What is the output of the following code?\n\nx = 5\ny = 'Hello'\nprint(x + y)", new String[]{"5Hello", "Error", "Hello5", "None"}, 1));
                list.add(new QuestionModel("Which function is used to take input from the user in Python?", new String[]{"scan()", "input()", "read()", "getInput()"}, 1));
                list.add(new QuestionModel("What is the correct syntax to open a file named 'file.txt' for reading in Python?", new String[]{"open('file.txt', 'r')", "open('file.txt')", "open('file.txt', 'w')", "open('file.txt', 'read')"}, 0));
                list.add(new QuestionModel("What is the output of the following code?\n\nx = 5\nprint('The value of x is ' + x)", new String[]{"The value of x is 5", "5", "'The value of x is ' + x", "Error"}, 3));
                list.add(new QuestionModel("What is the output of the following code?\n\nx = '5'\nprint(x * 2)", new String[]{"10", "55", "'55'", "None"}, 1));
                list.add(new QuestionModel("Which of the following is used to remove an item from a list in Python?", new String[]{"remove()", "pop()", "delete()", "clear()"}, 1));
                list.add(new QuestionModel("What is the output of the following code?\n\nx = [1, 2, 3]\nprint(x[3])", new String[]{"1", "2", "3", "Error"}, 3));
                list.add(new QuestionModel("Which module is used to work with regular expressions in Python?", new String[]{"regex", "re", "regexpy", "pyregex"}, 1));
                break;
            default:
                break;
        }
    }

}