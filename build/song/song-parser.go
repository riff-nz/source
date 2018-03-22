package song

import (
    "os"
    "fmt"
    "bufio"
    "strings"
    "github.com/ghodss/yaml"
	chordParser "../chord"
)

type Song struct {
    Name string					`json:"name"`
    Artist string 				`json:"artist"`
    Description string			`json:"description"`
    Level string 				`json:"level"`
    Tags []string				`json:"tags"`
    Links []map[string]string	`json:"links"`
    Chords []*chordParser.Chord `json:"chords"`
    Html string					`json:"html"`
}

func ParseSong(data string, chords* []chordParser.Chord) *Song {
	song := new(Song);
	reader := bufio.NewReader(strings.NewReader(data))
	
	readMeta(song, reader)
	readBody(song, reader, *chords)

	return song
	// error := WriteStringToFile("./output/the-cranberries/zombie.html", song.Html)
	// fmt.Printf("error opening file: %v\n",error)
}

func readMeta(song *Song, reader *bufio.Reader) {
	var content = ""

	for true {
		line, e := Readln(reader)
		if (e != nil) {
			// there were no meta informations
			break
		}
		if (strings.HasPrefix(line, "---")) {
			break
		}

		content += line + "\n"
	}

	// fmt.Println(content)
	err := yaml.Unmarshal([]byte(content), &song)
	if (err != nil) {
		fmt.Println("Problem when unmarshalling YAML", err)
	}
}

func readBody(song *Song, reader *bufio.Reader, allChords []chordParser.Chord) {
	var read bool = true
	var nextLine = ""
	var line = ""
	var e error
	var isThisFirstHashedSectionHeading = true

	chords := map[string]bool{}

	for read {
		if (nextLine != "") {
			line = nextLine
			nextLine = ""
		} else {
			line, e = Readln(reader)
			if (e != nil) {
				// eof
				isThisFirstHashedSectionHeading = closeSectionIfNeeded(song, isThisFirstHashedSectionHeading)

				// populate all used chords into Song
				fmt.Println("Processing song chords", chords)
				for chord := range chords { 
    				for _, alreadyAddedChord := range song.Chords {
    					if (chord == alreadyAddedChord.Name) {
    						chord = ""
    						break
    					}
    				}

					if (chord != "") {
						// find full chord
	    				for _, fullChord := range allChords {
							if (chord == fullChord.Name) {
								song.Chords = append(song.Chords, &fullChord)
								break				
							}
						}						
					}
				}
				return;
			}
		}

		if (strings.HasPrefix(line, "#")) {
			isThisFirstHashedSectionHeading = closeSectionIfNeeded(song, isThisFirstHashedSectionHeading)

			str := readLine(line, "# ")
			// class name for section
			section := "section"
			if (str != "") {
				section += " " + strings.ToLower(strings.Fields(str)[0])
			}
			song.Html += "<div class=\"" + section + "\">"
			song.Html += "\n  <h3>" + str + "</h3>\n"
			continue
		}

		if (strings.HasPrefix(line, "%")) {
			nextLine, e := Readln(reader)

			if (e != nil) {
				// keep going
			}
			
			var output string = ""
			var chord string = ""

			var chordsLine = readLine(line, "% ");
			var textLine = ""
			if (strings.HasPrefix(nextLine, ">")) {
				textLine = readLine(nextLine, "> ");
				nextLine = ""
			}

			for i, c := range chordsLine {
				if (c == ' ') {
					chords, chord, output = processChord(chords, textLine, chord, output)
				}

				if (c != ' ') {
					chord += string(c)
				}

				if (len(textLine) > i) {
					output += string(textLine[i])
				} else {
					output += "&nbsp;"
				}
			}
			// process the last chord on the line (no space after it)
			chords, chord, output = processChord(chords, textLine, chord, output)

			// when text line is longer than chordsline, just add the remaining texts...
			if (len(textLine) > len(chordsLine)) {
				// index := len(textLine) - len(chordsLine)
				output += textLine[(len(chordsLine)):]
			}
			// fmt.Println(output)

			song.Html += "  <div class=\"line"
			if (len(textLine) > 0) {
				song.Html += " text"
			}
			song.Html += "\">\n    " + output + "\n  </div>\n"
		}
	}
}

func processChord(chords map[string]bool, textLine string, chord string, output string) (map[string]bool, string, string) {
	if (chord != "") {
		index := len(output)
		if (!strings.HasSuffix(output, "&nbsp;") && len(textLine) > 0 && len(output) >= len(chord)) {
			index = len(output) - len(chord)
		}
		
		chords[chord] = true

		output = output[:index] + "<div is=\"chord\" name=\"" + chord + "\"></div>" + output[index:]
		chord = ""
	}
	return chords, chord, output
}

func readLine(line string, prefix string) string {
	tokens := strings.Split(line, prefix)
	if (tokens.len != 2) {
		fmt.Println("Problem when spliting line %s with prefix %s", line, prefix)
		os.Exit(1)
	}
	return tokens[1]
}

func parseMeta(line string) string {
    return strings.Split(line, ": ")[1]
}

func Readln(r *bufio.Reader) (string, error) {
  var (isPrefix bool = true
       err error = nil
       line, ln []byte
      )
  for isPrefix && err == nil {
      line, isPrefix, err = r.ReadLine()
      ln = append(ln, line...)
  }
  return string(ln),err
}

func closeSectionIfNeeded(song *Song, isThisFirstHashedSectionHeading bool) bool {
	if (isThisFirstHashedSectionHeading) {
		isThisFirstHashedSectionHeading = false
	} else {
		song.Html += "</div>\n\n"	
	}
	return isThisFirstHashedSectionHeading
}
