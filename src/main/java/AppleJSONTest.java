import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

/**
* Created with IntelliJ IDEA.
* User: jeffreyawest
* Date: 5/10/12
* Time: 10:10 AM
*/
public class AppleJSONTest
{
  public static void main(String[] args)
  {
    String JSON_STRING = "{\n" +
        "    AddDebugInfoToUserComments = 1;\n" +
        "    Orientation = 3;\n" +
        "    \"{Diagnostic}\" =     {\n" +
        "        AEAdjustedMinWeight = 48;\n" +
        "        AEAverage = 205;\n" +
        "        AECenterMean = 42;\n" +
        "        AELimitsReached = 0;\n" +
        "        AEMatrix = <2a2e2d2c 28281b2a 2f333131 2c211e2d 34373424 1e1d2334 3a3c391f 1d23364a 3c3f3d26 222a474e 2a3b443d 363f403f 1f374e4d 47464441 4260605d 56534a43>;\n" +
        "        AEOverallAverageLuma = 54;\n" +
        "        AEPatternMean = 54;\n" +
        "        AEPatternScene = 240;\n" +
        "        AEStable = 1;\n" +
        "        AFStable = 1;\n" +
        "        AFStatus = 4;\n" +
        "        AGC = 3004;\n" +
        "        AWBBGain = 282;\n" +
        "        AWBGGain = 64;\n" +
        "        AWBRGain = 169;\n" +
        "        AWBStable = 1;\n" +
        "        AccelerometerDeltas = \"(0.0130 0.0069 0.0206) @ 0.0200\";\n" +
        "        ActualNormalizedTorchBrightness = 0;\n" +
        "        AddDebugInfoToUserComments = 1;\n" +
        "        ApertureValue = 3;\n" +
        "        BrightnessValue = \"-0.6071138880873017\";\n" +
        "        Build = \"5.0 (9A5220p)\";\n" +
        "        CurrentFocusPosition = 169;\n" +
        "        DesiredNormalizedTorchBrightness = 0;\n" +
        "        ExposureBias = 0;\n" +
        "        ExposureTime = \"0.06646783334990554\";\n" +
        "        Fnumber = \"2.8\";\n" +
        "        FocalLength = \"3.85\";\n" +
        "        FocusBand = \"18.20 in [18.31, 22.37]\";\n" +
        "        FocusPeakSumArray =         (\n" +
        "            35754,\n" +
        "            36465,\n" +
        "            37206,\n" +
        "            37999,\n" +
        "            118240\n" +
        "        );\n" +
        "        FocusScan = \"(0,18.30) (29,18.12) (56,17.34) (81,18.03) (105,18.25) (127,18.64) (149,19.63) (169,19.79) (188,18.82) (206,16.98) 2.5s ago\";\n" +
        "        FocusScoresArray =         (\n" +
        "            1220616,\n" +
        "            1290343,\n" +
        "            1357167,\n" +
        "            1436925,\n" +
        "            22890259\n" +
        "        );\n" +
        "        FocusWindow =         (\n" +
        "            365,\n" +
        "            320,\n" +
        "            270,\n" +
        "            360\n" +
        "        );\n" +
        "        FullyExposed = 0;\n" +
        "        GlobalShutterFlag = 0;\n" +
        "        HistogramBinMode = 3;\n" +
        "        HistogramImageDataType = 1;\n" +
        "        ISOSpeedRating = 769;\n" +
        "        LEDMode = 0;\n" +
        "        MeteringMode = 5;\n" +
        "        NoiseReduction = \"S:34 C:14 Y:6\";\n" +
        "        NormalizedSNR = \"23.18870519852102\";\n" +
        "        OneFrameAEMode = 1;\n" +
        "        OverflowOccurred = 0;\n" +
        "        PowerBlur = 1;\n" +
        "        PreviousFocusBand = \"20.24 in [0.00, 0.00]\";\n" +
        "        PreviousFocusScan = \"(0,17.38) (29,16.04) (56,15.90) (81,15.37) (105,14.96) 8.5s ago\";\n" +
        "        RollingShutterSkew = 64445;\n" +
        "        SNR = \"23.20661427469983\";\n" +
        "        SensorID = 22097;\n" +
        "        Sharpness = 11;\n" +
        "        ShutterSpeedValue = \"3.911199862602335\";\n" +
        "        TimeMachine = 1;\n" +
        "        TimeMachineSelection = 3;\n" +
        "        ToneMapping = \"black:0.09491 scale:1.161 s0:0.361 s1:0.200 (0.00 0.75 0.25) saturation:1.05 warm:0.02\";\n" +
        "        WYSIWYG = 1;\n" +
        "        WaitForAF = 1;\n" +
        "        WaitForAFSkipCount = 0;\n" +
        "        ispDGain = 295;\n" +
        "        sensorDGain = 256;\n" +
        "    };\n" +
        "    \"{Exif}\" =     {\n" +
        "        ApertureValue = \"2.970853654340484\";\n" +
        "        ColorSpace = 1;\n" +
        "        DateTimeDigitized = \"2011:06:09 12:48:06\";\n" +
        "        DateTimeOriginal = \"2011:06:09 12:48:06\";\n" +
        "        ExposureMode = 0;\n" +
        "        ExposureProgram = 2;\n" +
        "        ExposureTime = \"0.06666666666666667\";\n" +
        "        FNumber = \"2.8\";\n" +
        "        Flash = 16;\n" +
        "        FocalLength = \"3.85\";\n" +
        "        ISOSpeedRatings =         (\n" +
        "            800\n" +
        "        );\n" +
        "        MeteringMode = 5;\n" +
        "        PixelXDimension = 2592;\n" +
        "        PixelYDimension = 1936;\n" +
        "        SceneType = 1;\n" +
        "        SensingMethod = 2;\n" +
        "        Sharpness = 1;\n" +
        "        ShutterSpeedValue = \"3.911199862602335\";\n" +
        "        SubjectArea =         (\n" +
        "            1295,\n" +
        "            967,\n" +
        "            699,\n" +
        "            696\n" +
        "        );\n" +
        "        WhiteBalance = 0;\n" +
        "    };\n" +
        "    \"{TIFF}\" =     {\n" +
        "        DateTime = \"2011:06:09 12:48:06\";\n" +
        "        Make = Apple;\n" +
        "        Model = \"iPhone 4\";\n" +
        "        Software = \"5.0\";\n" +
        "        XResolution = 72;\n" +
        "        YResolution = 72;\n" +
        "    };\n" +
        '}';

    JSON_STRING = JSON_STRING.replaceAll("=", ":");
    JSON_STRING = JSON_STRING.replaceAll(";", ",");
    JSON_STRING = JSON_STRING.replaceAll("<", "\"<");
    JSON_STRING = JSON_STRING.replaceAll(">", ">\"");
    JSON_STRING = JSON_STRING.replaceAll("\\(", "[");
    JSON_STRING = JSON_STRING.replaceAll("\\)", "]");
    JSON_STRING = JSON_STRING.replaceAll("\"\\{", "\"");
    JSON_STRING = JSON_STRING.replaceAll("\\}\"", "\"");
    JSON_STRING = JSON_STRING.replaceAll("\\,\\s*\\},", "\n},");
    JSON_STRING = JSON_STRING.replaceAll("\\:\\s*Apple,", ": \"Apple\",");
    System.out.println(JSON_STRING);
    JSON_STRING = JSON_STRING.replaceAll("\\}\\,\\s*\\}", "}\n}");

    ObjectMapper mapper = new ObjectMapper();
    mapper.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, false);
    mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);

    try
    {
      Map<String, Object> jsonData = mapper.readValue(JSON_STRING, Map.class);

      displayJSONMap(jsonData, 0);
    }
    catch (IOException e)
    {
      e.printStackTrace();
    }
  }

  private static void displayJSONMap(Map<String, Object> jsonData, int level)
  {
    StringBuilder indent = new StringBuilder(2 * level);

    for (int x = 0; x < level; x++)
    {
      indent.append("  ");
    }

    for (Map.Entry<String, Object> stringObjectEntry : jsonData.entrySet())
    {
      String str = stringObjectEntry.getKey();
      Object obj = stringObjectEntry.getValue();

      if (obj instanceof Map)
      {
        System.out.println(indent + str + ": " + " (" + obj.getClass().getSimpleName() + ')');
        displayJSONMap((Map<String, Object>) obj, level + 1);
      }
      else
      {
        System.out.println(indent + str + ": " + obj + " (" + obj.getClass().getSimpleName() + ')');
      }

    }
  }
}
