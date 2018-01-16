package chord

import (
// 	"fmt"
    "bufio"
    "strings"
)

type Chord struct {
    Name string	  		`json:"name"`
    Key string        `json:"key"`
    Strings string 	  `json:"strings"`
    Fingers string	  `json:"fingers"`
}

func ParseChord(data string) *Chord {
	chord := new(Chord);
	reader := bufio.NewReader(strings.NewReader(data))
	
	readMeta(chord, reader)

	return chord
	// error := WriteStringToFile("./output/the-cranberries/zombie.html", song.Html)
	// fmt.Printf("error opening file: %v\n",error)
}

func readMeta(chord *Chord, reader *bufio.Reader) {
	var read bool = true;

	for read {
		line, e := Readln(reader)
		if (e != nil) {
			// there were no meta informations
			return;
		}

		if strings.Contains(line, "name") {
      chord.Name = parseMeta(line)

      chord.Key = chord.Name
      chord.Key = strings.Replace(chord.Key, "#", "-sharp-", -1)
      chord.Key = strings.Replace(chord.Key, "/", "-", -1)
      chord.Key = strings.TrimSuffix(chord.Key, "-")
    }
		
		if strings.Contains(line, "strings") {
      guitarStrings := parseMeta(line)
      if (guitarStrings != "") {
				chord.Strings = guitarStrings
      }
    }

		if strings.Contains(line, "fingers") {
      guitarFingers := parseMeta(line)
      if (guitarFingers != "") {
		    chord.Fingers = guitarFingers
      }
    }
	}
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