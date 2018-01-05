package song

import (
 	"fmt"
    "bufio"
    "strings"
)

type Song struct {
    Name string			`json:"name"`
    Artist string 		`json:"artist"`
    Chords []string 	`json:"chords"`
    Html string			`json:"html"`
}

func ParseSong(data string) *Song {
	song := new(Song);
	reader := bufio.NewReader(strings.NewReader(data))
	
	readMeta(song, reader)
	readBody(song, reader)

	return song
	// error := WriteStringToFile("./output/the-cranberries/zombie.html", song.Html)
	// fmt.Printf("error opening file: %v\n",error)
}

func readMeta(song *Song, reader *bufio.Reader) {
	var read bool = true;

	for read {
		line, e := Readln(reader)
		if (e != nil) {
			// there were no meta informations
			return;
		}
		if (strings.HasPrefix(line, "---")) {
			read = false;
		}

		if strings.Contains(line, "song") {
            song.Name = parseMeta(line)
        }
		if strings.Contains(line, "artist") {
            song.Artist = parseMeta(line)
        }
	}
}

func readBody(song *Song, reader *bufio.Reader) {
	var read bool = true
	var nextLine = ""
	var line = ""
	var e error
	chords := map[string]bool{}

	for read {
		if (nextLine != "") {
			line = nextLine
			nextLine = ""
		} else {
			line, e = Readln(reader)
			if (e != nil) {
				// eof
				// populate all used chords into Song
				fmt.Println("Processing song chords", chords)
				for k := range chords { 
    				song.Chords = append(song.Chords, k)	
				}
				return;
			}
		}

		if (strings.HasPrefix(line, "#")) {
			song.Html += "\n<h3>" + readLine(line, "# ") + "</h3>\n"
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

			song.Html += "<div class=\"line"
			if (len(textLine) > 0) {
				song.Html += " text"
			}
			song.Html += "\">\n  " + output + "\n</div>\n"
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
    return strings.Split(line, prefix)[1]
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