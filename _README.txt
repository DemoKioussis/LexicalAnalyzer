open project in java ide

to analyze different files, change path of "codePath" to another valid path

2 text documents will be generated in the output folder 
	- tokens.txt for list of valid tokens
	- errors.txt for all lexemes that were not identified

I used the sourcecode for the builder pattern I used for this assignment as the test case
and there are no errors aside form characters that I have not defined (: ' " ? ! @ # $ % ^ & )

I think this is a valid test case because it contains many things that we are testing for, 
and I included a list of strings in comments at the end which is not real code but is still
able to pick out the correct tokens for

In addition, you can input your own lexemes or sentences into the debug console and press enter 
This is run it through the lexical analyzer and store it within itself. To view the result of this,
enter the reserved word "show" to output the tokenized contents of the L.A.